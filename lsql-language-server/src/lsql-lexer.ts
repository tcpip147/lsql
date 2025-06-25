import { Token, TokenType } from './lsql-types';

export class LsqlLexer {
  private pos = 0;
  private input: string;
  tokens: Token[] = [];

  constructor(input: string) {
    this.input = input;
    this.tokenize();
  }

  getch(offset: number) {
    return this.input[this.pos + offset];
  }

  getTokenType(offset: number) {
    if (this.tokens[this.tokens.length + offset] == null) {
      return -1;
    }
    return this.tokens[this.tokens.length + offset].type;
  }

  match(regex: RegExp, ch: string) {
    if (ch == null) {
      return false;
    }
    return regex.test(ch);
  }

  tokenize() {
    while (this.pos < this.input.length) {
      const ch = this.input[this.pos];

      // IdSymbol or OptionSymbol
      if (ch == '@') {
        if (this.getch(-1) == null || this.getTokenType(-1) == TokenType.Linebreak) {
          if (this.getch(1) == 'i' && this.getch(2) == 'd' && (this.getch(3) == ' ' || this.getch(3) == null)) {
            this.tokens.push({
              type: TokenType.IdSymbol,
              text: this.input.slice(this.pos, this.pos + 3),
              start: this.pos,
              end: this.pos + 3,
            });
            this.pos += 3;
            continue;
          }
          let i = 1;
          let symbol = '';
          while (this.match(/[a-zA-Z0-9]/, this.getch(i))) {
            symbol += this.getch(i);
            i++;
          }
          if (symbol != '') {
            this.tokens.push({
              type: TokenType.DescriptionSymbol,
              text: this.input.slice(this.pos, this.pos + symbol.length + 1),
              start: this.pos,
              end: this.pos + symbol.length + 1,
            });
            this.pos += symbol.length + 1;
            continue;
          }
        }
      }

      // IdValue
      if (this.getTokenType(-2) == TokenType.IdSymbol && this.getTokenType(-1) == TokenType.Whitespace) {
        let i = 0;
        let value = '';
        while (this.match(/[a-zA-Z0-9._]/, this.getch(i))) {
          value += this.getch(i);
          i++;
        }

        if (value != '') {
          this.tokens.push({
            type: TokenType.IdValue,
            text: this.input.slice(this.pos, this.pos + value.length),
            start: this.pos,
            end: this.pos + value.length,
          });
          this.pos += value.length;
          continue;
        }
      }

      // OptionValue
      if (this.getTokenType(-2) == TokenType.DescriptionSymbol && this.getTokenType(-1) == TokenType.Whitespace) {
        let i = 0;
        let value = '';
        while (this.match(/[^\r\n]/, this.getch(i))) {
          value += this.getch(i);
          i++;
        }

        if (value != '') {
          this.tokens.push({
            type: TokenType.DescriptionValue,
            text: this.input.slice(this.pos, this.pos + value.length),
            start: this.pos,
            end: this.pos + value.length,
          });
          this.pos += value.length;
          continue;
        }
      }

      // Linebreak
      if (ch == '\n') {
        this.tokens.push({
          type: TokenType.Linebreak,
          text: this.input.slice(this.pos, this.pos + 1),
          start: this.pos,
          end: this.pos + 1,
        });
        this.pos += 1;
        continue;
      } else if (ch == '\r') {
        if (this.getch(1) == '\n') {
          this.tokens.push({
            type: TokenType.Linebreak,
            text: this.input.slice(this.pos, this.pos + 2),
            start: this.pos,
            end: this.pos + 2,
          });
          this.pos += 2;
          continue;
        } else {
          this.tokens.push({
            type: TokenType.Linebreak,
            text: this.input.slice(this.pos, this.pos + 1),
            start: this.pos,
            end: this.pos + 1,
          });
          this.pos += 1;
          continue;
        }
      }

      // Whitespace
      if (ch == ' ') {
        this.tokens.push({
          type: TokenType.Whitespace,
          text: this.input.slice(this.pos, this.pos + 1),
          start: this.pos,
          end: this.pos + 1,
        });
        this.pos += 1;
        continue;
      }

      // Sql
      let i = 0;
      let value = '';
      while (true) {
        if (this.getch(i) == null) {
          break;
        }
        if (this.reachToIdSymbol(i)) {
          break;
        }
        value += this.getch(i);
        i++;
      }
      if (value != '') {
        this.tokens.push({
          type: TokenType.Sql,
          text: this.input.slice(this.pos, this.pos + value.length),
          start: this.pos,
          end: this.pos + value.length,
        });
        this.pos += value.length;
        continue;
      }

      // Unknown
      this.tokens.push({
        type: TokenType.Unknown,
        text: this.input.slice(this.pos, this.pos + 1),
        start: this.pos,
        end: this.pos + 1,
      });
      this.pos += 1;
    }
  }

  reachToIdSymbol(i: number): boolean {
    if (this.getch(i) == '\r') {
      i++;
    }
    if (this.getch(i) != '\n') {
      return false;
    }
    if (this.getch(i + 1) == '\r') {
      i++;
    }
    if (this.getch(i + 1) != '\n') {
      return false;
    }
    if (this.getch(i + 2) == '@' && this.getch(i + 3) == 'i' && this.getch(i + 4) == 'd') {
      return true;
    }
    return false;
  }
}
