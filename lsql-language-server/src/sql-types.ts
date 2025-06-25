export enum TokenType {
  BlockComment = 0,
  LineComment = 1,
  String = 2,
  ExtraString = 3,
  BindingVariable = 4,
  Keyword = 5,
  Unknown = 6,
}

export interface Token {
  type: TokenType;
  text: string;
  start: number;
  end: number;
}

export class TrieNode {
  children: Map<string, TrieNode>;
  isFinal: boolean;

  constructor() {
    this.children = new Map();
    this.isFinal = false;
  }
}
