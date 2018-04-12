#!/bin/bash
set -e
npm install
rm -fr *.vsix
npm run vsce-package
code --uninstall-extension Pivotal.vscode-wordcheck || echo "WARN: Can't uninstall old version."
rm -fr ~/.vscode/extensions/Pivotal.vscode-wordcheck*
code --install-extension *.vsix