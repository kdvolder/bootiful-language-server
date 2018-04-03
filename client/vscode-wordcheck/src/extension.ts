'use strict';

import * as VSCode from 'vscode';
import * as commons from '@pivotal-tools/commons-vscode';

const WORDCHECK_LANGUAGE_ID = "wordcheck";

/** Called when extension is activated */
export function activate(context: VSCode.ExtensionContext) {
    let options : commons.ActivatorOptions = {
        DEBUG : false,
        CONNECT_TO_LS: false,
        extensionId: 'vscode-wordcheck',
        jvmHeap: "48m",
        clientOptions: {
            documentSelector: [ WORDCHECK_LANGUAGE_ID ]
        }
    };
    commons.activate(options, context);
}
