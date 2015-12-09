package com.linkprise.pojo.generator;

/**
 * ������ɵĴ����ʽ���ո�TAB
 * 
 * @author <a href="mailto:wt47@live.com">linkprise.com Ȯ��</a>
 * @version 1.0.0
 * ����������������������������������������������������������������������<br/>
 * �޶�����                 �޶���            ����<br/>
 * 2016-1-18       linkprise.com Ȯ��            ����<br/>
 */
public class TabStack
{
  private StringBuilder content;
  private int stackSize;

  public TabStack()
  {
    this(0);
  }

  public TabStack(int size) {
    this.stackSize = size;
    this.content = new StringBuilder();
  }

  public StringBuilder getContent() {
    return this.content;
  }

  public int stackSize() {
    return this.stackSize;
  }

  public void reset() {
    this.stackSize = 0;
  }

  public void push() {
    push(1);
  }

  public void push(int count) {
    this.stackSize += count;
    appendEOL();
  }

  public void pop() {
    pop(1);
  }

  public void pop(int count) {
    if (this.stackSize - count > 0)
      this.stackSize -= count;
    else {
      this.stackSize = 0;
    }
    appendEOL();
  }

  public void append(String value) {
    this.content.append(value);
  }

  public void append(int value) {
    this.content.append(value);
  }

  public void append(long value) {
    this.content.append(value);
  }

  public void appendEOL() {
    appendEOL(1);
  }

  public void appendEOL(int count) {
    for (int i = 0; i < count; i++) {
      this.content.append(GenUtil.LINE_END);
    }
    for (int i = 0; i < this.stackSize; i++)
      this.content.append('\t');
  }

  public String toString()
  {
    return this.content.toString();
  }
}