import { Token, TokenType, TrieNode } from './sql-types';

export class SqlLexer {
  pos!: number;
  input!: string;
  tokens!: Token[];
  root: TrieNode;

  constructor() {
    this.root = new TrieNode();
    const keywords = [
      'ACCESS',
      'ADD',
      'AFTER',
      'AGER',
      'ALL',
      'ALTER',
      'AND',
      'ANY',
      'APPLY',
      'ARCHIVE',
      'ARCHIVELOG',
      'AS',
      'ASC',
      'AT',
      'AUDIT',
      'AUTHID',
      'AUTOEXTEND',
      'BACKUP',
      'BEFORE',
      'BEGIN',
      'BETWEEN',
      'BODY',
      'BULK',
      'BY',
      'CASCADE',
      'CASE',
      'CAST',
      'CHECK',
      'CHECKPOINT',
      'CLOSE',
      'COALESCE',
      'COLUMN',
      'COMMENT',
      'COMMIT',
      'COMPILE',
      'COMPRESS',
      'COMPRESSED',
      'CONJOIN',
      'CONNECT',
      'CONNECT_BY_ROOT',
      'CONSTANT',
      'CONSTRAINT',
      'CONSTRAINTS',
      'CONTINUE',
      'CREATE',
      'CROSS',
      'CUBE',
      'CURRENT_USER',
      'CURSOR',
      'CYCLE',
      'DATABASE',
      'DECLARE',
      'DECRYPT',
      'DEFAULT',
      'DEFINER',
      'DELAUDIT',
      'DELETE',
      'DEQUEUE',
      'DESC',
      'DETERMINISTIC',
      'DIRECTORY',
      'DISABLE',
      'DISASTER',
      'DISCONNECT',
      'DISJOIN',
      'DISTINCT',
      'DROP',
      'DUMP_CALLSTACKS',
      'EACH',
      'ELSE',
      'ELSEIF',
      'ELSIF',
      'ENABLE',
      'END',
      'ENQUEUE',
      'ESCAPE',
      'EXCEPTION',
      'EXEC',
      'EXECUTE',
      'EXISTS',
      'EXIT',
      'EXTENT',
      'EXTENTSIZE',
      'FALSE',
      'FETCH',
      'FIFO',
      'FIXED',
      'FLASHBACK',
      'FLUSH',
      'FLUSHER',
      'FOLLOWING',
      'FOR',
      'FOREIGN',
      'FROM',
      'FULL',
      'FUNCTION',
      'GOTO',
      'GRANT',
      'GROUP',
      'HAVING',
      'IDENTIFIED',
      'IF',
      'IN',
      'INDEX',
      'INITRANS',
      'INNER',
      'INSERT',
      'INSTEAD',
      'INTERSECT',
      'INTO',
      'IS',
      'ISOLATION',
      'JOIN',
      'KEEP',
      'KEY',
      'LANGUAGE',
      'LATERAL',
      'LEFT',
      'LESS',
      'LEVEL',
      'LIBRARY',
      'LIFO',
      'LIKE',
      'LIMIT',
      'LINK',
      'LINKER',
      'LOB',
      'LOCAL',
      'LOCALUNIQUE',
      'LOCK',
      'LOGANCHOR',
      'LOGGING',
      'LOOP',
      'MATERIALIZED',
      'MAXROWS',
      'MAXTRANS',
      'MERGE',
      'MINUS',
      'MODE',
      'MODIFY',
      'MOVE',
      'MOVEMENT',
      'NEW',
      'NOARCHIVELOG',
      'NOAUDIT',
      'NOCOPY',
      'NOCYCLE',
      'NOLOGGING',
      'NOPARALLEL',
      'NOT',
      'NULL',
      'NULLS',
      'OF',
      'OFF',
      'OFFLINE',
      'OLD',
      'ON',
      'ONLINE',
      'OPEN',
      'OR',
      'ORDER',
      'OTHERS',
      'OUT',
      'OUTER',
      'OVER',
      'PACKAGE',
      'PARALLEL',
      'PARAMETERS',
      'PARTITION',
      'PARTITIONS',
      'PIVOT',
      'PRECEDING',
      'PRIMARY',
      'PRIOR',
      'PRIVILEGES',
      'PROCEDURE',
      'PURGE',
      'QUEUE',
      'RAISE',
      'READ',
      'REBUILD',
      'RECOVER',
      'REFERENCES',
      'REFERENCING',
      'REMOTE_TABLE',
      'REMOTE_TABLE_STORE',
      'REMOVE',
      'RENAME',
      'REORGANIZE',
      'REPLACE',
      'REPLICATION',
      'RETURN',
      'RETURNING',
      'REVOKE',
      'RIGHT',
      'ROLLBACK',
      'ROLLUP',
      'ROW',
      'ROWCOUNT',
      'ROWNUM',
      'ROWTYPE',
      'SAVEPOINT',
      'SEGMENT',
      'SELECT',
      'SEQUENCE',
      'SESSION',
      'SET',
      'SHARD',
      'SHRINK_MEMPOOL',
      'SOME',
      'SPECIFICATION',
      'SPLIT',
      'SQLCODE',
      'SQLERRM',
      'START',
      'STATEMENT',
      'STEP',
      'STORAGE',
      'STORE',
      'SUPPLEMENTAL',
      'SYNONYM',
      'TABLE',
      'TABLESPACE',
      'TEMPORARY',
      'THAN',
      'THEN',
      'TO',
      'TOP',
      'TRIGGER',
      'TRUE',
      'TRUNCATE',
      'TYPE',
      'TYPESET',
      'UNCOMPRESSED',
      'UNION',
      'UNIQUE',
      'UNLOCK',
      'UNPIVOT',
      'UNTIL',
      'UPDATE',
      'USING',
      'VALUES',
      'VARIABLE',
      'VARIABLE_LARGE',
      'VC2COLL',
      'VIEW',
      'VOLATILE',
      'WAIT',
      'WHEN',
      'WHENEVER',
      'WHERE',
      'WHILE',
      'WITH',
      'WITHIN',
      'WORK',
      'WRAPPED',
      'WRITE',
      '_PROWID',
      'CACHE',
      'NOCACHE',
    ];
    for (const keyword of keywords) {
      this.insert(keyword);
    }
  }

  insert(word: string): void {
    word = word.toLowerCase();
    let current = this.root;
    for (const char of word) {
      if (!current.children.has(char)) {
        current.children.set(char, new TrieNode());
      }
      current = current.children.get(char)!;
    }
    current.isFinal = true;
  }

  accepts(word: string): boolean {
    word = word.toLowerCase();
    let current = this.root;
    for (const char of word) {
      if (!current.children.has(char)) {
        return false;
      }
      current = current.children.get(char)!;
    }
    return current.isFinal;
  }

  getch(offset: number) {
    return this.input[this.pos + offset];
  }

  tokenize(input: string) {
    this.input = input;
    this.pos = 0;
    this.tokens = [];
    while (this.pos < this.input.length) {
      // String
      if (this.getch(0) == "'") {
        const anchor = this.pos;
        this.pos++;
        while (this.pos < this.input.length) {
          if (this.getch(0) == "'") {
            this.pos++;
            break;
          }
          this.pos++;
        }
        this.tokens.push({
          type: TokenType.String,
          text: this.input.slice(anchor, this.pos),
          start: anchor,
          end: this.pos,
        });
        continue;
      }

      // ExtraString
      if (this.getch(0) == '"') {
        const anchor = this.pos;
        this.pos++;
        while (this.pos < this.input.length) {
          if (this.getch(0) == '"') {
            this.pos++;
            break;
          }
          this.pos++;
        }
        this.tokens.push({
          type: TokenType.ExtraString,
          text: this.input.slice(anchor, this.pos),
          start: anchor,
          end: this.pos,
        });
        continue;
      }

      // BlockComment
      if (this.getch(0) == '/' && this.getch(1) == '*') {
        const anchor = this.pos;
        this.pos += 2;
        while (this.pos < this.input.length) {
          if (this.getch(-1) == '*' && this.getch(0) == '/') {
            this.pos++;
            break;
          }
          this.pos++;
        }
        this.tokens.push({
          type: TokenType.BlockComment,
          text: this.input.slice(anchor, this.pos),
          start: anchor,
          end: this.pos,
        });
        continue;
      }

      // LineComment
      if (this.getch(0) == '-' && this.getch(1) == '-') {
        const anchor = this.pos;
        let end = this.pos;
        this.pos += 2;
        while (this.pos < this.input.length) {
          if (this.getch(0) == '\n') {
            end = this.pos;
            this.pos++;
            break;
          } else if (this.getch(0) == '\r') {
            end = this.pos;
            if (this.getch(1) == '\n') {
              this.pos += 2;
            } else {
              this.pos++;
            }
            break;
          }
          this.pos++;
        }
        this.tokens.push({
          type: TokenType.LineComment,
          text: this.input.slice(anchor, end),
          start: anchor,
          end: end,
        });
        continue;
      }

      // BindingVariable
      if (this.getch(0) == ':' && /[a-zA-Z_]/.test(this.getch(1))) {
        const anchor = this.pos;
        this.pos += 2;
        while (/[a-zA-Z_0-9]/.test(this.getch(0)) && this.pos < this.input.length) {
          this.pos++;
        }
        this.tokens.push({
          type: TokenType.BindingVariable,
          text: this.input.slice(anchor, this.pos),
          start: anchor,
          end: this.pos,
        });
        continue;
      }

      // Keyword
      if ((/[^a-zA-Z]/.test(this.getch(-1)) || this.getch(-1) == null) && /[a-zA-Z]/.test(this.getch(0))) {
        let text = '';
        const anchor = this.pos;
        while (/[a-zA-Z0-9]/.test(this.getch(0)) && this.pos < this.input.length) {
          text += this.getch(0);
          this.pos++;
        }
        if (this.accepts(text)) {
          this.tokens.push({
            type: TokenType.Keyword,
            text: this.input.slice(anchor, this.pos),
            start: anchor,
            end: this.pos,
          });
          continue;
        }
        this.pos = anchor;
      }

      this.pos++;
    }

    return this.tokens;
  }
}
