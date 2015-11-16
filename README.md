# Continuations

This project is an implementation of continuation-passing style Byte Buffer consumption.

When implementing complex protocols, it's often the case that imperative style code turns out to be
complicated to read, extend and compose. By using CPS, you can create branches, repeats, optional
fields

# Example

Here's a simple example of creating a CPS protocol parser. You simply specify steps in the required
order one after another, with branching as if you were writing `case` statements, and end up
having a function that converts `ByteBuf` into desired format:

```java
Function<ByteBuf, List<Number>> fn = ContinuationImpl
  // Reads the first byte and constructs an Array that will collect all the seen values
  .readByte(first -> {
    List<Number> o = new ArrayList<Number>();
    o.add(first);
    return o;
  })
          // Constructs two branches: first one will be executed if first read byte is `1`,
  .branch(list -> list.get(0).intValue() == 1,
          new BranchStart<List<Number>>()
            .readByte((List<Number> list, Byte firstBranch) -> {
              list.add(firstBranch);
              return list;
            }),
          // Second branch will be executed if first read byte is `2`,
          list -> list.get(0).intValue() == 2,
          new BranchStart<List<Number>>()
            .readByte((List<Number> list, Byte firstBranch) -> {
              list.add((byte) (firstBranch + 1));
              return list;
            }))
   // Will receive the list that was composed by one of the branches and proceed with consuming the buffer
  .readInt((list, integer) -> {
    list.add(integer);
    return list;
  })
  .toFn();
```

Basically, you construct the set of nested lambdas, that will be executed one after another.
When branching, evaluation is delayed and further steps are taken only for the branch that
matches the predicate.

# Project status

So far it's a proof of concept. It was first required to create branching (for example, for cases like
protocol versioning or possible branches depending on data types).

# Further Steps

  * Ready protocol spec right from the constructed function.
  * Optional field consumption (field that either gets consumed and buffer rewinded or buffer remains on the
    previous position)
  * Repeated fields

# License

Copyright(C) 2015-2016 Alex Petrov

Double licensed under the [Eclipse Public License](http://www.eclipse.org/legal/epl-v10.html) (the same as Clojure) or
the [Apache Public License 2.0](http://www.apache.org/licenses/LICENSE-2.0.html).
