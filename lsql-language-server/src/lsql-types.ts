export interface Range {
  start: {
    line: number;
    character: number;
  };
  end: {
    line: number;
    character: number;
  };
}

export type Position = {
  offset: number;
  line: number;
  character: number;
};

export interface ASTNodeBase {
  type: string;
  start?: Position;
  end?: Position;
  parent?: ASTNode;
}

export interface ASTNode extends ASTNodeBase {
  children?: ASTNode[];
  [key: string]: any;
}

export enum TokenType {
  IdSymbol = 0,
  IdValue = 1,
  DescriptionSymbol = 2,
  DescriptionValue = 3,
  Sql = 4,
  Whitespace = 5,
  Linebreak = 6,
  Unknown = 7,
}

export interface Token {
  type: TokenType;
  text: string;
  start: number;
  end: number;
}

export class SyntaxError extends Error {
  pos: number;

  constructor(message: string, pos: number) {
    super(message);
    this.pos = pos;
  }
}