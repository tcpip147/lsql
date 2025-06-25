import { TextDocument as TxtDoc } from 'vscode-languageserver-textdocument';
import { createConnection, DidChangeWatchedFilesNotification, InitializeParams, ProposedFeatures, TextDocuments, TextDocumentSyncKind } from 'vscode-languageserver/node';
import { LsqlFile } from './lsql-file';
import { SyntaxError } from './lsql-types';
import { SqlLexer } from './sql-lexer';

const connection = createConnection(ProposedFeatures.all);
const documents = new TextDocuments(TxtDoc);

connection.onInitialize((params: InitializeParams) => {
  return {
    capabilities: {
      textDocumentSync: TextDocumentSyncKind.Full,
    },
  };
});

connection.onNotification('$/parseLsql', (event) => {
  const file = new LsqlFile(event.text);
  connection.sendNotification('$/parseLsql', {
    requestId: event.requestId,
    queryList: file.getQueries(),
    errors: file.errors.map((error: SyntaxError) => {
      return {
        message: error.message,
        pos: error.pos,
      };
    }),
  });
});

connection.onNotification('$/tokenizeSql', (event) => {
  const lexer = new SqlLexer();
  connection.sendNotification('$/tokenizeSql', {
    requestId: event.requestId,
    tokens: lexer.tokenize(event.text),
  });
});

documents.listen(connection);
connection.listen();
