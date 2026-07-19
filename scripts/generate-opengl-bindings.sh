#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
SDK_PATH="${SDKROOT:-$(xcrun --show-sdk-path)}"
INCLUDE_ROOT="$ROOT_DIR/target/jextract-include"
OUT_DIR="$ROOT_DIR/src/main/java"

rm -rf "$INCLUDE_ROOT"
mkdir -p "$INCLUDE_ROOT"
ln -s "$SDK_PATH/System/Library/Frameworks/OpenGL.framework/Headers" "$INCLUDE_ROOT/OpenGL"

jextract \
  --output "$OUT_DIR" \
  --target-package org.java.desktop.ffmawt.gl \
  --header-class-name gl_h \
  --library OpenGL \
  --include-dir "$INCLUDE_ROOT" \
  --include-function glClearColor \
  --include-function glClear \
  --include-function glViewport \
  --include-function glBegin \
  --include-function glEnd \
  --include-function glVertex2f \
  --include-function glColor3f \
  --include-function glFlush \
  --include-constant GL_COLOR_BUFFER_BIT \
  --include-constant GL_TRIANGLES \
  "$INCLUDE_ROOT/OpenGL/gl.h"
