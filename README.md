# IDeA: project D - Dynamic language
## Target platform: Interpreter
## Implementation language/tool: Java, hand-written parser

## Test cases

### 1. Hello world program

Input:

```
print "Hello world!"; // Hello world!
```

Output:

```
Hello world!
```

### 2. Operations check with integer/real

Input:

```
var realVar1 := 2.5;
var realVar2 := 4.245;

var integerVar1 := 3;
var integerVar2 := 22;

var result;

result := integerVar1 + integerVar2;
print result is int; // true
print result; // 25

result := realVar1 + realVar2;
print result is real; // true
print result; // 6.745

result := integerVar1 + realVar2;
print result is real; // true
print result; // 7.245

result := realVar1 + integerVar2;
print result is int; // false
print result; // 24.5

print + integerVar1; // 4
print - integerVar2; // 21
print + realVar1; // 3.5
print - realVar2; // 3.245

```

Output:

```
true
25
true
6.745
true
7.245
false
24.5
4
21
3.5
3.245
```

### 3. Integer with integer, real with real comparison and with each other

Input:

```
var realVar1 := 2.5;
var realVar2 := 4.245;

var integerVar1 := 3;
var integerVar2 := 22;

print integerVar1 > integerVar2; // false
print integerVar1 >= integerVar2; // false
print integerVar2 = integerVar1; // false
print integerVar2 /= integerVar1; // true
print integerVar1 < integerVar2; // true
print integerVar1 <= integerVar2; // true

print realVar1 > realVar2; // false
print realVar1 >= realVar2; // false
print realVar1 = realVar2; // false
print realVar1 /= realVar2; // true
print realVar1 < realVar2; // true
print realVar1 <= realVar2; // true

print integerVar1 > realVar2; // false
print integerVar1 >= realVar2; // false
print integerVar1 = realVar2; // false
print integerVar1 /= realVar2; // true
print integerVar1 < realVar2; // true
print integerVar1 <= realVar2; // true

```

Output:

```
false
false
false
true
true
true
false
false
false
true
true
true
false
false
false
true
true
true
```

### 4. And/or/xor/not

Input:

```
var realVar1 := 2.5;
var realVar2 := 4.245;

var integerVar1 := 3;
var integerVar2 := 22;

// output: 2
if realVar1 = 2.5 and integerVar1 /= 3 then
    print 1;
else
    if realVar2 /= 3.0 or integerVar2 = 22 then
        print 2;
    else
        return 4;
    end;
end;

print integerVar1 xor integerVar2; // false

```

Output:

```
2
false
```

### 5. Dynamic type testing

Input:

```
var testVar := 5;
print testVar is string; // false
print testVar is real; // false
print testVar is int; // true

testVar := 1.5;
print testVar is string; // false
print testVar is real; // true
print testVar is int; //false

testVar := 'string';
print testVar is string; // true
print testVar is real; // false
print testVar is int; // false

```

Output:

```
false
false
true
false
true
false
true
false
false
```

### 6. A simple loop in which the sum is calculated

Input:

```
var sum := 0;
var array := [1, 2, 3, 4, 5];
for element in array
loop
  sum := sum + element;
end;
print sum; // 15

```

Output:

```
15
```

### 7. A function for exponentiation using a while loop

Input:

```
var pow := func (base, exponent) is
  var result := 1;
  var i := 0;
  while i < exponent loop
    result := result * base;
    i := i + 1;
  end;
  return result;

end;

print pow(2, 3); // 8
```

Output:

```
8
```

### 8. Basic usage of if ... else statement

Input:

```
var userAge := readInt();
if userAge < 18 then
  print "You are under 18";
else
  print "You are over 18";
end;

```

Output depends on input.

### 9. Declaring a function in variables and using them in an if statement

Input:

```
var add := func (a, b) is
  return a + b;
end;

var mult := func (a, b) is
  return a * b;
end;

var num1 := readInt();
var num2 := readInt();

if num1 > num2 then
  print mult(num1, num2);
else
  print add(num1, num2);
end;

```

Output depends on input. If num1 > num2, then we multiply them, otherwise sum them up.

### 10. Loop inside the loop

Input:

```
var array1 := [1, 2, 3, 4, 5];
var array2 := [10, 20, 30, 50, 40];

var result := 1;
for elem1 in array1 loop
  for elem2 in array2 loop
    result := result + elem1 + elem2;
  end;
end;

print result; // 826
```

Output:

```
826
```

### 11. Check the input, output

Input:

```
var userInput := readInt(); // Reads an integer from input
print "You entered: ", userInput;

```

Output depends on input.

### 12. Check that the keys of adjacent values in an Array can differ by more than one

Input:

```
var t := []; // empty array declaration
t[10] := 25;
t[100] := 30;
t[1000] := {a:=1,b:=2.7};
print t[10], t[100], t[1000]; // Should print 25, 30, "{a:=1,b:=2.7}"

var myArray := [1, 3, 4, 6];  // Example array with non-sequential keys
print myArray[1], myArray[3], myArray[4]; // Should print 1, 4, 6

```

Output:

```
25
30
{a=1, b=2.7}
1
4
6
```

### 13. Create a tuple and specify pairs without a value

Input:

```
var y := "test";
var myTuple := { x := 5, y, z := 10 }; // 'y' is a pair without a value
print myTuple.x, myTuple.2, myTuple.z; // Should print 5, test, 10
```

Output:

```
5
test
10
```

### 14. Save the function as a variable

Input:

```
var add := func (a, b) is 
    return a + b;
end;
  
print add(3, 5); // Should print 8

```

Output:

```
8
```

### 15. Check the recursion

Input:

```
var factorial := func (n) is
    if n = 0 then
        return 1;
    else
        return n * factorial(n - 1);
    end;
end;
  
print factorial(5); // Should print 120

```

Output:

```
120
```

## Contacts:

* Ilya Zubkov - i.zubkov@innopolis.university
* Denis Mikhailov - d.mikhailov@innopolis.university
* Anton Chulakov - a.chulakov@innopolis.university
