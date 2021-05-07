#!/bin/bash
PWD=$(pwd)
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
if [ "$PWD" != "$DIR" ]
then
        echo "This script does not support execution from other directories"
        exit 1
fi

git subtree pull --prefix swattr_core https://github.com/ArDoCo/Core.git main --squash
git subtree pull --prefix swattr_parent https://github.com/ArDoCo/ArDoCo-parent.git master --squash