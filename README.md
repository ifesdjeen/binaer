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
	.readByte(first -> {
			List<Number> o = new ArrayList<Number>();
			o.add(first);
			return o;
		})
	.readInt((List<Number> list, Integer second) -> {
			list.add(second);
			return list;
		})
	.readLong((List<Number> list, Long third) -> {
			list.add(third);
			return list;
		})
	.branch(list -> list.get(0).intValue() == 1)
	.readByte((list, firstBranch) -> {
			list.add(firstBranch);
			return list;
		})
	.end((i -> i))
	.otherwise(list -> list.get(0).intValue() == 2)
	.readByte((list, secondBranch) -> {
			list.add((byte) (secondBranch + 1));
			return list;
		})
	.end((i -> i))
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
  * Repeated fields ()
  * Nested branches / repeats (should already work by now though)
  * API cleanup

# License

Copyright(C) 2015-2016 Alex Petrov

Double licensed under the [Eclipse Public License](http://www.eclipse.org/legal/epl-v10.html) (the same as Clojure) or
the [Apache Public License 2.0](http://www.apache.org/licenses/LICENSE-2.0.html).
