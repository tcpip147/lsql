import { ASTNode } from './lsql-types';

const traverse = function (node: ASTNode, visit: (node: ASTNode) => void) {
  visit(node);
  if (node.children) {
    for (const child of node.children) {
      traverse(child, visit);
    }
  }
};

const getOffset = function (text: string, line: number, character: number): number {
  let offset = 0;
  let currentLine = 0;
  const length = text.length;

  while (currentLine < line && offset < length) {
    if (text[offset] === '\r') {
      if (offset + 1 < length && text[offset + 1] === '\n') {
        offset += 2;
      } else {
        offset += 1;
      }
      currentLine++;
    } else if (text[offset] === '\n') {
      offset += 1;
      currentLine++;
    } else {
      offset += 1;
    }
  }
  return offset + character;
};

const getLineNumber = function(text:string, pos: number) {
  
}

export { getOffset, traverse };
