lexer grammar LsqlLexer;

channels { ERROR }

IdSymbol: '@id';
OptionSymbol: '@' [a-z]+;

Br: ('\n' | '\r\n');
Ws: ' ';
Value: {_input.LA(-1) == ' '}? ~[\r\n]*;
Line: {_input.LA(1) != '@' && _input.LA(-1) == '\n'}? ~[\r\n]*;

UnexpectedCharacter: . -> channel(ERROR);