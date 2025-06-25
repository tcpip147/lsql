import fs from 'fs';
import path from 'path';
import { LsqlFile } from '../src/lsql-file';

test('success01', () => {
  const content = fs.readFileSync(path.join(__dirname, 'resources', 'success01.lsql'), 'utf8');
  const file = new LsqlFile(content);
  let query = file.getQueries()[0];
  expect(query.id).toBe('test.selectUser');
  expect(query.description).toBe('Select User');
  expect(query.sql).toBe('SELECT *\r\n  FROM USER');
});

test('success02', () => {
  const content = fs.readFileSync(path.join(__dirname, 'resources', 'success02.lsql'), 'utf8');
  const file = new LsqlFile(content);
  let query = file.getQueries()[0];
  expect(query.id).toBe('test.selectUser');
  expect(query.description).toBe('Select User');
  expect(query.sql).toBe('SELECT *\r\n  FROM USER');
  query = file.getQueries()[1];
  expect(query.id).toBe('test.selectDepartment');
  expect(query.description).toBe('Select Department');
  expect(query.sql).toBe(`SELECT *\r\n  FROM DEPARTMENT\r\n\r\n WHERE ID = '1'`);
});

test('error01', () => {
  const content = fs.readFileSync(path.join(__dirname, 'resources', 'error01.lsql'), 'utf8');
  const file = new LsqlFile(content);
  let query = file.getQueries()[0];
  expect(query.id).toBe('test.selectDepartment');
  expect(query.description).toBe('Select Department');
  expect(query.sql).toBe(`SELECT *\r\n  FROM DEPARTMENT\r\n\r\n WHERE ID = '1'`);
  let errors = file.getErrors();
  expect(errors[0].message).toBe('Expected token type IdValue at position 4');
});

test('error02', () => {
  const content = fs.readFileSync(path.join(__dirname, 'resources', 'error02.lsql'), 'utf8');
  const file = new LsqlFile(content);
  let errors = file.getErrors();
  expect(errors[0].message).toBe('Expected token type IdValue at position 4');
  expect(errors[1].message).toBe('Expected token type IdValue at position 63');
});
