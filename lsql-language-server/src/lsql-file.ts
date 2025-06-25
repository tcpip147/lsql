import { LsqlLexer } from './lsql-lexer';
import { LsqlParser } from './lsql-parser';
import { ASTNode, SyntaxError } from './lsql-types';
import { traverse } from './utils';

export class LsqlQuery {
  id: string = '';
  description: string = '';
  sql: string = '';
  start: number = 0;
}

export class LsqlFile {
  tree: ASTNode;
  errors: SyntaxError[];

  constructor(content: string) {
    const lexer = new LsqlLexer(content);
    const parser = new LsqlParser(lexer.tokens);
    this.tree = parser.statements;
    this.errors = parser.errors;
  }

  getQueries(): LsqlQuery[] {
    const queryList: LsqlQuery[] = [];
    let query: LsqlQuery;
    traverse(this.tree, (node) => {
      if (node.type === 'Statement') {
        query = new LsqlQuery();
        queryList.push(query);
      } else if (node.type === 'IdValue') {
        query.id = node.text;
      } else if (node.type === 'DescriptionValue') {
        query.description = node.text;
      } else if (node.type === 'Sql') {
        query.start = node.startOffset;
        query.sql = node.text;
      }
    });    
    return queryList;
  }

  getErrors(): SyntaxError[] {
    return this.errors;
  }

  traverse(node: ASTNode, visitor: (node: ASTNode) => void) {
    visitor(node);
    if (node.children) {
      for (const child of node.children) {
        traverse(child, visitor);
      }
    }
  }
}
