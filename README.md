# Continuation

`Continuation` is a utility for composing flexible binary protocol parsers by using the Continuation
Passing Style programming.

When implementing complex protocols, it's often the case that imperative style code turns out to be
complicated to read, extend and compose. By using CPS, you can create branches, repeats, optional
fields

# General idea

In order to construct a flexible parser, you should parse things in an
incremental fashion and keep all parts of the parser interchangeable.
Continuation is helping you to create an incremental parser (e.g. one that will
execute one step at a time, and each step will receive the result of execution
of the previous step).

Let's look at a simple example of the Pascal-style Strings. This is probably the
simplest part of the binary protocol one can implement. It's also called
`netstrings` sometimes. The idea behind it is that you encode the String by
encoding it with `int` that indicates how many characters there are in the
encoded String, followed by the chars themselves:

```
0   4                 10
+---+-----------------+
| 5 |   a b c d e f   |
+---+-----------------+
```

We create a `Continuation`. Our first function receives `Integer` `(1)`, which
is the size of the String that follows it. The function `(2)` will "extract" the
length of the string. In our case it's `identity`. Now, as we know the length of
our String, we can read it with `(3)`, which will discard the String length and
return just the resulting String.


```java
Function<ByteBuf, String> continuation =
    Continuation.startWithInt(Function.identity())   // (1) Integer -> Integer: reads the Integer, that specifies the amount of chars in string
                .readString(Function.identity(),     // (2) Integer -> Integer: Function that extracts the length of the string
                            (String s) -> s)         // (3) String -> String: Return the decoded string itself
                .toFn();

continuation.apply(Unpooled.buffer()
                           .writeInt(6)
                           .writeBytes("abcdef".getBytes()));
// => "abcdef"
```

Continuation constructs a function that, given a `ByteBuf` instance, will pass
it through the chain of `Integer -> Integer -> String` functions and return a
`String` result in the end. In real world you would be first constructing a
`Frame Header`, then `Frame`, then adding `Frame Body`to it etc.

# Branches

With branch, you can choose how you decode the object you're working on. For
example, you're decoding a protocol that has two data types: `date` and `string`

```java
Continuation<Void, Object> continuation =
     Continuation
        .startWithByte(Function.identity())
        .branch(
          // If the type is 1, which is our `date`
          (Byte type) -> type == (byte) 1,
          Continuation.startWithLong((Byte type, Long l) -> new Date(l)),

           // If the type is 2, which is our `string`
          (Byte type) -> type == (byte) 2,
          Continuation.startWithInt((Byte type, Integer stringLength) -> stringLength)
                      .readString((Integer integer, String s) -> s,
                                  Function.identity()));

continuation.toFn().apply(null, Unpooled.buffer()
                                        .writeByte(1)
                                        .writeLong(System.currentTimeMillis()));
                                        //) Parses a date:
// => Fri Nov 20 16:44:49 CET 2015

continuation.toFn().apply(null, Unpooled.buffer()
                                        .writeByte(2)
                                        .writeInt(6)
                                        .writeBytes("abcdef".getBytes())));
// Parses a string:
// => "abcdef"
```

# Repeated Fields

Many protocols require something like repeated fields. For example, if we'd like to create a protocol
parser that consumes a list of strings, we can do it as follows:

```java
// Define a "repeated" part - our netstring protocol
Continuation<Integer, String> netString =
      Continuation.startWithInt((Integer a_, Integer i) -> i)
                  .readString((Integer integer, String s) -> s,
                              Function.identity());

// And define a parser for repeated netstrings:
// We'll first find how many strings there are, and then parse each one of them separately
Continuation<Void, List<String>> continuation =
      Continuation.startWithInt(Function.identity())
                  .repeat(netString,
                          Function.identity(),
                          (prev, l) -> l);

continuation.toFn(() -> null)
            .apply(Unpooled.buffer()
                           .writeInt(3)
                           .writeInt(6)
                           .writeBytes("abcdef".getBytes())
                           .writeInt(5)
                           .writeBytes("fghij".getBytes())
                           .writeInt(4)
                           .writeBytes("klmn".getBytes())));
// => ["abcdef", "fghij", "klmn"];
```

# Project status

So far it's a proof of concept. It was first required to create branching (for
example, for cases like protocol versioning or possible branches depending on
data types).

Every operator you can see (branches, repeated fields and so on) can be nested
and combined in any fashion.

# Further Steps

  * Protocol Spec Blueprints generated from consuming functions
  * Optional field consumption (field that either gets consumed and buffer
    rewinded or buffer remains on the previous position)
  * More data types
  * More combiners
  * More default protocol implementations

# License

Copyright(C) 2015-2016 Alex Petrov

Double licensed under the [Eclipse Public License](http://www.eclipse.org/legal/epl-v10.html) (the same as Clojure) or
the [Apache Public License 2.0](http://www.apache.org/licenses/LICENSE-2.0.html).
