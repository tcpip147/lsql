parser grammar LsqlParser;

options {
    tokenVocab=LsqlLexer;
}

statements
    : statement (Br statement)*
    | EOF
    ;

statement
    : header (Br | EOF) sql (Br | EOF)
    ;

header
    : idAttribute optionAttribute*
    ;

idAttribute
    : IdSymbol Ws Value Br
    ;

optionAttribute
    : OptionSymbol Ws? Value? Br
    ;

sql
    : (Line | Br | Ws)*
    ;