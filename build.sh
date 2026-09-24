#!/usr/bin/env bash
set -euo pipefail

##############
# Script env #
##############

SCRIPT="$0"
SCRIPT_PATH="${BASH_SOURCE[0]}"
while [ -L "${SCRIPT_PATH}" ]; do
  SCRIPT_DIR="$(cd -P "$(dirname "${SCRIPT_PATH}")" >/dev/null 2>&1 && pwd)"
  SCRIPT_PATH="$(readlink "${SCRIPT_PATH}")"
  [[ ${SCRIPT_PATH} != /* ]] && SCRIPT_PATH="${SCRIPT_DIR}/${SCRIPT_PATH}"
done
SCRIPT_PATH="$(readlink -f "${SCRIPT_PATH}")"
SCRIPT_NAME="$(basename "$SCRIPT_PATH")"
SCRIPT_DIR="$(cd -P "$(dirname -- "${SCRIPT_PATH}")" >/dev/null 2>&1 && pwd)"

OCI="podman"
if ! command -v podman >/dev/null 2>&1
then
	OCI="docker"
fi

BUILD_CONTAINER_IMAGE="registry.gitlab.com/fdroid/fdroidserver:buildserver"


#########
# Setup #
#########

setup() {
	echo "Setup build environment using $OCI container"
	
	local oci_opts=()
	local script_opts=()

	if [[ -n "$keystore" ]]
	then
		oci_opts+=( 
			--volume "$keystore":/work/signing.jks:ro
		)
		script_opts+=( 
			--keystore "/work/signing.jks"
			--keystore-pswd "$keystore_pswd"
		)
	fi

	chmod +x "$SCRIPT_DIR/gradlew"
	"$OCI" run --rm -it --tty \
		--volume "$SCRIPT_DIR":/work/src \
		--workdir /work \
		--entrypoint /usr/bin/env \
		"${oci_opts[@]}" \
		"$BUILD_CONTAINER_IMAGE" -- \
		bash "/work/src/$SCRIPT_NAME" --target build "${script_opts[@]}"
}


#########
# Build #
#########

build() {
	echo "Build by $(id) on $(uname -a)"

	echo "Setup android SDK"	
	: "${ANDROID_SDK_ROOT:="/tmp/android-sdk"}"
	export ANDROID_SDK_ROOT
	export ANDROID_HOME="$ANDROID_SDK_ROOT"
	echo "Accept sdkmanager licenses"
	echo y | sdkmanager --licenses

	echo "Build unsigned apk"
	./gradlew clean check assembleRelease
	local apk_path apk_name apk_rname apk_tdir
	apk_path="$(find "$SCRIPT_DIR/app/build/outputs/apk/release/" -maxdepth 1 -type f -name '*universial*release*.apk' | sort | head -n1)"
	apk_name="$(basename "$apk_path")"
	apk_rname="ref_$apk_name"
	apk_tdir="$(mktemp --directory --suffix="-apk_tdir")"
	cp "$apk_path" "$apk_tdir/$apk_name"

	echo "Test for reproducible builds"
	sleep 5
	./gradlew clean assembleRelease
	cp "$apk_path" "$apk_tdir/$apk_rname"
	if cmp "$apk_tdir/$apk_name" "$apk_tdir/$apk_rname"
	then
		echo "------------------------"
		echo "✔️ Build is reproducible"
		echo "------------------------"
	else
		echo "----------------------------"
		echo "❌ Build is not reproducible"
		echo "----------------------------"
	fi

	# Optional apk signing
	if [[ -n "$keystore" ]]
	then
		echo "Sign apk"
		apksigner sign --in "$apk_path" --ks "$keystore" --ks-pass "stdin" <<< "$keystore_pswd"
		apksigner verify --verbose --print-certs "$apk_path"
	fi

	rm --recursive --force "$apk_tdir"
}


########
# Main #
########

usage() {
	echo "Usage: $SCRIPT [options]"
	echo "	--keystore - Set the keystore used for signing (optional - requires single key and password)"
	echo "	--keystore-pswd - The keystore password (default: env:KEYSTORE_PSWD)"
	echo "	--target - Run target: setup, build (default: setup)"
	echo "	--help - Show this help"
	exit 2
}

help() {
	usage
}

target=""
keystore=""
keystore_pswd=""

while [[ $# -gt 0 ]]
do
	case "$1" in
		--target)
			: "${target:="$2"}"
			shift 2
			;;
		--keystore)
			: "${keystore:="$2"}"
			shift 2
			;;
		--keystore-pswd)
			: "${keystore_pswd:="$2"}"
			shift 2
			;;
		--help)
			target="help"
			shift
			;;
		*)
			echo "Unknown option '$1'"
			exit 1
			;;
	esac
done
: "${target:="setup"}"

if [[ -n "$keystore" && ( ! -f "$keystore" || ! -r "$keystore" ) ]]
then
	echo "Error: '$keystore' is not a readable file" >&2
	exit 1
elif [[ -n "$keystore" ]]
then
	: "${keystore_pswd:="${KEYSTORE_PSWD:-}"}"
	if [[ -z "$keystore_pswd" ]]
	then
		read -s -r -p "Keystore password ($(basename "$keystore")): " keystore_pswd
	fi
fi

echo "Run target: $target"
(
	cd "$SCRIPT_DIR"
	"$target"
)
