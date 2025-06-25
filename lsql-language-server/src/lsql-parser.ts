import { ASTNode, SyntaxError, Token, TokenType } from './lsql-types';

export class LsqlParser {
  tokens: Token[];
  statements: ASTNode;
  pos = 0;
  errors: SyntaxError[] = [];

  constructor(tokens: Token[]) {
    this.tokens = tokens;
    this.statements = {
      type: 'Statements',
      children: [],
    };
    this.parse();
  }

  parse(): void {
    try {
      while (this.pos < this.tokens.length) {
        if (this.tokens[this.pos].type == TokenType.IdSymbol) {
          const statement = {
            type: 'Statement',
            startOffset: this.tokens[this.pos].start,
            children: [],
          };
          this.required(statement, TokenType.IdSymbol);
          this.required(statement, TokenType.Whitespace);
          this.required(statement, TokenType.IdValue);
          this.required(statement, TokenType.Linebreak);
          this.required(statement, TokenType.DescriptionSymbol);
          this.required(statement, TokenType.Whitespace);
          if (this.expect(TokenType.DescriptionValue)) {
            this.append(statement, TokenType.DescriptionValue);
          }
          if (this.pos < this.tokens.length) {
            this.required(statement, TokenType.Linebreak);
          }
          if (this.pos < this.tokens.length) {
            this.required(statement, TokenType.Linebreak);
          }
          if (this.expect(TokenType.Sql)) {
            this.append(statement, TokenType.Sql);
          }
          this.statements.children!.push(statement);
        }
        this.pos++;
      }
    } catch (e: any) {
      this.errors.push(e);
      this.gotoNextIdSymbol();
      this.parse();
    }
  }

  expect(tokenType: TokenType): boolean {
    return this.pos < this.tokens.length && this.tokens[this.pos].type == tokenType;
  }

  append(parent: ASTNode, tokenType: TokenType) {
    if (this.tokens[this.pos].type == tokenType) {
      parent.children!.push(
        this.createNode(TokenType[tokenType], {
          text: this.tokens[this.pos].text,
          startOffset: this.tokens[this.pos].start,
        }),
      );
      this.pos++;
    }
  }

  required(parent: ASTNode, tokenType: TokenType) {
    if (this.expect(tokenType)) {
      this.append(parent, tokenType);
    } else {
      throw new SyntaxError(`Expected token type ${TokenType[tokenType]} at position ${this.tokens[this.pos].start}`, this.tokens[this.pos].start);
    }
  }

  gotoNextIdSymbol(): boolean {
    while (this.pos < this.tokens.length && this.tokens[this.pos].type != TokenType.IdSymbol) {
      this.pos++;
    }
    return this.pos < this.tokens.length;
  }

  createNode(type: string, fields: Partial<ASTNode> = {}, children: ASTNode[] = []): ASTNode {
    const node: ASTNode = {
      type,
      ...fields,
      children,
    };
    for (const child of children) {
      child.parent = node;
    }
    return node;
  }
}
