#!/bin/bash
#
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

[ ! -n "${QUARKS:-}" ] && QUARKS=../..

#
# Application name identifies the running process.  For the scope of this
# script it is also the name of the main application class.
#
APP=TerminateAfterNTuples

#
# Command to start the application.
#
COMMAND="java -cp ${QUARKS}/samples/lib/quarks.samples.topology.jar \
quarks.samples.topology.${APP}"


_usage() {
  echo -e "Usage: ${0} [dir] \n\
  Runs an application while saving its stdout to dir/${APP}.out, its stderr to dir/${APP}.err, and its process id to dir/${APP}.pid.\n\
  \n\
  This script starts the application only if it cannot find its pid. If the process id indicated by dir/${APP}.pid exists, then the script will not start the application.\n\
  \n\
  If dir is not specified, then the stdout, stderr, and pid files are saved into the current directory.\n\
"
}

# _get_pid program [pidfile]
# Set $pid to pids from /tmp* for {program}.
# $pid should be declared local in the caller.
_get_pid() {
  local base=${1##*/}
  local pid_file=${2:-/tmp/$base.pid}

  pid=
  if [ -f "$pid_file" ] ; then
    local p

    [ ! -r "$pid_file" ] && return 1 # "Cannot access pid file"

    p=$(cat "$pid_file")
    [ -z "${p//[0-9]/}" ] && [ -d "/proc/$p" ] && pid="$p"
    if [ -n "$pid" ]; then
        return 0
    fi
    return 2 # "Program is not running but pid file exists"
  fi
  return 3 # "Program pid file does not exist"
}

# _readlink path
# Output the full path, replacement for readlink -f
_readlink() {
  (
  pushd ${1%/*} > /dev/null 2>&1
  echo $PWD/${1##*/}
  popd > /dev/null 2>&1
  )
}

# _dirname path
# Get the directory name, replacement for dirname.
_dirname() {
  echo ${1%/*}
}

_error() {
  echo $1; exit 1
}


if [ "${1:-.}" == "--help" ]; then
  _usage
  exit 1
fi

out_dir=${1:-.}

if [[ ! -d ${out_dir} || ${out_dir:0:1} != '/' ]]; then
  ## out_dir as absolute path
  out_dir=$(_readlink ${out_dir})
  out_dir=$(_dirname ${out_dir})
fi

pid_file=${out_dir}/${APP}.pid
out_file=${out_dir}/${APP}.out
err_file=${out_dir}/${APP}.err
pid=

_get_pid $APP $pid_file
RC="$?"
[ $RC == "1" ] && _error "Cannot access pid file $pid_file"

if [ -z "$pid" ]; then
  ${COMMAND} >> $out_file 2>> $err_file &
  pid="$!"
  echo $pid >| $pid_file
  echo -e "The program was restarted with command:\n\
${COMMAND}\n\
Process id: $pid"
else
  echo "The program's process id is $pid"
fi
