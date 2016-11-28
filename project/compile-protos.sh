#!/usr/bin/env bash

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
BASE_DIR=$SCRIPT_DIR/..

SRC_DIR=$BASE_DIR/src/test/proto
DST_DIR=$BASE_DIR/src/test/java

protoc -I=$SRC_DIR --java_out=$DST_DIR $SRC_DIR/messages.proto
