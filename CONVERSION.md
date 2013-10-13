Syntax conversion table
=======================
Between Matlab, Armadillo C++ and ArmadilloJava

**Note:** Currently, only real-valued dense matrices with double precision are supported.


Matrix initialisation / generation
----------------------------------

Matlab                     | Armadillo C++              | ArmadilloJava                   | Notes
---------------------------|----------------------------|---------------------------------|------
                           | A()                        | A()                             | 
                           | A(n, m)                    | A(n, m)                         | 
                           | A(n, m, fill::none)        | A(n, m, Fill.NONE)              | 
A = [ ... ]                | A << ... << endr;          | A(new double[][]{ ... })        | 
                           | A(" ... ");                |                                 | *Not suppported*
zeros(n, m)                | A(n, m, fill::zeros)       | A(n, m, Fill.ZEROS)             | 
zeros(n, m)                | zeros(n, m)                | Mat.zeros(n, m)                 | 
ones(n, m)                 | A(n, m, fill::ones)        | A(n, m, Fill.ONES)              | 
ones(n, m)                 | ones(n, m)                 | Mat.ones(n, m)                  | 
eye(n, m)                  | A(n, m, fill::eye)         | A(n, m, Fill.EYE)               | 
eye(n, m)                  | eye(n, m)                  | Mat.eye(n, m)                   | 
randu(n, m)                | A(n, m, fill::randu)       | A(n, m, Fill.RANDU, rng)        | **Note:** rng is a random number generator.
randu(n, m)                | randu(n, m)                | Mat.randu(n, m, rng)            | 
randn(n, m)                | A(n, m, fill::randn)       | A(n, m, Fill.RANDN, rng)        | 
randn(n, m)                | randn(n, m)                | Mat.randn(n, m, rng)            | 
                           | repmat(A, n, m)            | Mat.repmat(A, n, m)             | 
toeplitz(A [, B])          | toeplitz(A [, B])          | Mat.toeplitz(A [, B])           | 
                           | circ_toeplitz(A)           | Mat.circ_toeplitz(A)            | **Matlab:** [Matlab Central File Excange - Circulant matrix](http://www.mathworks.com/matlabcentral/fileexchange/22858)
linspace(a, b [, n])       | linspace(a, b [, n])       | Mat.linspace(a, b [, n])        | **Note:** Generates a column vector. **Default:** n = 100 


Matrix attributes
-----------------

Matlab                     | Armadillo C++              | ArmadilloJava                   | Notes
---------------------------|----------------------------|---------------------------------|------
size(A, 1)                 | A.n_rows                   | A.n_rows                        | 
size(A, 2)                 | A.n_cols                   | A.n_cols                        | 
numel(A)                   | A.n_elem                   | A.n_elem                        | 


Element access 
--------------

              | Inline operations
--------------|-------------------
Matlab        | A(...) = A(...) Operator B
Armadillo C++ | A(...) Operator= B
ArmadilloJava | A(..., Op.Operator, B)

Matlab                     | Armadillo C++              | ArmadilloJava                   | Notes
---------------------------|----------------------------|---------------------------------|------
A(i+1, j+1)                | A(i, j)                    | A(i, j)                         | 
A(n)                       | A(n)                       | A(n)                            | 


Member functions
----------------

### Submatrix views

              | Inline operations
--------------|-------------------
Matlab        | A(...) = A(...) Operator B
Armadillo C++ | A.... Operator= B
ArmadilloJava | A....(Op.Operator, B)

#### Contiguous views

Matlab                     | Armadillo C++                        | ArmadilloJava                   | Notes
---------------------------|--------------------------------------|---------------------------------|------
A(:, j)                    | A.col(j)                             | A.col(j)                        | 
A(:, j)                    | A(span::all, j)                      | A.col(j)                        | 
                           | A.unsafe_col(j)                      |                                 | *Not suppported*
A(i, :)                    | A.row(i)                             | A.row(i)                        | 
A(i, :)                    | A(i, span::all)                      | A.row(i)                        | 
A(:, a:b)                  | A.cols(a, b)                         | A.cols(a, b)                    | 
A(i, a:b)                  | A(i, span(a, b))                     | A.cols(a, b, i)                 | 
A(a:b, :)                  | A.rows(a, b)                         | A.rows(a, b)                    | 
A(a:b, j)                  | A(span(a, b), j)                     | A.rows(a, b, j)                 | 
A(ai:bi, aj:bj)            | A.submat(ai, bi, aj, bj)             | A.submat(ai, bi, aj, bj)        | 
A(ai:bi, aj:bj)            | A.submat(span(ai, bi), span(aj, bj)) | A.submat(ai, bi, aj, bj)        | 
A(ai:bi, aj:bj)            | A(span(ai, bi), span(aj, bj))        | A.submat(ai, bi, aj, bj)        | 
A(a:b)                     | A.subvec(a, b)                       | A.subvec(a, b)                  | **Note:** A is a vector.
A(a:b)                     | A(span(a, b))                        | A.subvec(a, b)                  | **Note:** A is a vector.

                           
#### Non-contiguous views

Matlab                     | Armadillo C++              | ArmadilloJava                   | Notes
---------------------------|----------------------------|---------------------------------|------
A(I)                       | A.elem(I)                  | A.elem(I)                       | **Note:** I is a vector.
A(I)                       | A(I)                       | A.elem(I)                       | **Note:** I is a vector.
A(:, J)                    | A.cols(J)                  | A.cols(J)                       | **Note:** J is a vector.
A(I, :)                    | A.rows(I)                  | A.rows(I)                       | **Note:** I is a vector.
A(I, J)                    | A.submat(I, J)             | A.submat(I, J)                  | **Note:** I, J are vectors.
A(I, J)                    | A(I, J)                    | A.submat(I, J)                  | **Note:** I, J are vectors.

                           
### Internal data access
**Not recommend unless you know what you are doing.**
ArmadilloJava uses [Efficient Java Matrix Library](https://code.google.com/p/efficient-java-matrix-library/) internally.

Matlab                     | Armadillo C++              | ArmadilloJava                   | Notes
---------------------------|----------------------------|---------------------------------|------
                           | A.memptr()                 | A.memptr()                      | 
                           | A.colptr()                 |                                 | *Not suppported*

                           
### Inline initilisation

Matlab                     | Armadillo C++              | ArmadilloJava                   | Notes
---------------------------|----------------------------|---------------------------------|------
A = zeros(size(A))         | A.zeros()                  | A.zeros()                       | 
A = zeros(n , 1)           | A.zeros(n)                 | A.zeros(n)                      | 
A = zeros(n , m)           | A.zeros(n, m)              | A.zeros(n , m)                  | 
A = ones(size(A))          | A.ones()                   | A.ones()                        | 
A = ones(n , 1)            | A.ones(n )                 | A.ones(n)                       | 
A = ones(n , m)            | A.ones(n, m)               | A.ones(n, m)                    | 
A = eye(size(A))           | A.eye()                    | A.eye()                         | 
A = eye(n, m)              | A.eye(n, m)                | A.eye(n, m)                     | 
A = randu(size(A))         | A.randu()                  | A.randu()                       | 
A = randu(n, 1)            | A.randu(n)                 | A.randu(n)                      | 
A = randu(n, m)            | A.randu(n, m)              | A.randu(n, m)                   | 
A = randn(size(A))         | A.randn()                  | A.randn()                       | 
A = randn(n, 1)            | A.randn(n)                 | A.randn(n)                      | 
A = randn(n, m)            | A.randn(n, m)              | A.randn(n, m)                   | 
A(:) = v                   | A.fill(v)                  | A.fill(v)                       | 
                           
                           
### Row/Column insert/remove

Matlab                                                 | Armadillo C++              | ArmadilloJava                   | Notes
-------------------------------------------------------|----------------------------|---------------------------------|------
A = [A(1:i-1, :); B; A(i+1:end, :)]                    | A.insert_rows(i, B)        | A.insert_rows(i, B)             | **Note:** B is a vector.
A = [A(1:i-1, :); zeros(n, size(A, 2)); A(i+1:end, :)] | A.insert_rows(i, n [, z])  | A.insert_rows(i, n [, z])       | **Default:** z = true
A = [A(:, 1:j-1) B A(end:j+1, :)]                      | A.insert_cols(j, B)        | A.insert_cols(j, B)             | **Note:** B is a vector.
A = [A(:, 1:j-1) zeros(size(A, 1), n); A(end.j+1, :)]  | A.insert_cols(j, n [, z])  | A.insert_cols(j, n [, z])       | **Default:** z = true
A = [A(1:i-1, :); A(i+1:end, :)]                       | A.shed_row(i)              | A.shed_row(i)                   | 
A = [A(1:a-1, :); A(b+1:end, :)]                       | A.shed_rows(a, b)          | A.shed_rows(a, b)               | 
A = [A(:, 1:j-1) A(end.j+1, :)]                        | A.shed_col(j)              | A.shed_col(j)                   | 
A = [A(:, 1:a-1) A(end.b+1, :)]                        | A.shed_cols(a, b)          | A.shed_cols(a, b)               | 
                           
                           
### Swap functions
Matlab                     | Armadillo C++              | ArmadilloJava                   | Notes
---------------------------|----------------------------|---------------------------------|------
                           | A.swap(A)                  | A.swap(A)                       | 
                           | A.swap_rows(i1, i2)        | A.swap_rows(i1, i2)             | 
                           | A.swap_cols(j1, j2)        | A.swap_cols(j1, j2)             | 

                           
### Is functions

Matlab                     | Armadillo C++              | ArmadilloJava                   | Notes
---------------------------|----------------------------|---------------------------------|------
isempty(A)                 | A.is_empty()               | A.is_empty()                    | 
isfinite(A)                | A.is_finite()              | A.is_finite()                   | 
                           | A.is_square()              | A.is_square()                   | **Matlab:** S = size(A); (length(S) < 3) && (S(1) == S(2))
isvector(A)                | A.is_vec()                 | A.is_vec()                      | 
iscolumn(A)                | A.is_colvec()              | A.is_colvec()                   | 
isrow(A)                   | A.is_rowvec()              | A.is_rowvec()                   | 
                           
                           
### Bound checks

Matlab                     | Armadillo C++                          | ArmadilloJava                   | Notes
---------------------------|----------------------------------------|---------------------------------|------
                           | A.in_range(n)                          | A.in_range(n)                   | 
                           | A.in_range(span(a, b))                 | A.in_range(span(a, b))          | 
                           | A.in_range(i, j)                       | A.in_range(i, j)                | 
                           | A.in_range(span(ai, bi), span(aj, bj)) | A.in_range(ai, bi, aj, bj)      | 
                           
                           
### Inline statistics

Matlab                     | Armadillo C++              | ArmadilloJava                   | Notes
---------------------------|----------------------------|---------------------------------|------
                           | A.min()                    | A.min()                         | 
                           | A.min(n)                   | A.min(n)                        | 
                           | A.min(i, j)                | A.min(i, j)                     | 
                           | A.max()                    | A.max()                         | 
                           | A.max(n)                   | A.max(n)                        | 
                           | A.max(i, j)                | A.max(i, j)                     |
                           
                           
### Reshape/resize
Matlab                     | Armadillo C++              | ArmadilloJava                   | Notes
---------------------------|----------------------------|---------------------------------|------
                           | A.reshape(n, m [, d])      | A.reshape(n, m [, d])           | 
                           | A.resize(n [, m])          | A.resize(n [, m])               | 
                           | A.set_size(n [, m])        | A.set_size(n [, m])             | 
                           
                           
### Text output functions

Matlab                     | Armadillo C++              | ArmadilloJava                   | Notes
---------------------------|----------------------------|---------------------------------|------
                           | A.print([h])               | A.print([h])                    | 
                           | A.print(s [, h])           | A.print(s [, h])                | 
                           | A.raw_print([h])           | A.raw_print([h])                | 
                           | A.raw_print(s [, h])       | A.raw_print(s [, h])            | 
                           
                           
### File input/output

Matlab                     | Armadillo C++              | ArmadilloJava                   | Notes
---------------------------|----------------------------|---------------------------------|------
                           | A.save(n [, t])            | A.save(n [, t])                 | 
                           | A.save(s [, t])            | A.save(s [, t])                 | 
                           | A.load(n [, t])            | A.load(n [, t])                 | 
                           | A.load(s [, t])            | A.load(s [, t])                 | 
                           | A.quiet_save(n [, t])      | A.quiet_save(n [, t])           | 
                           | A.quiet_save(s [, t])      | A.quiet_save(s [, t])           | 
                           | A.quiet_load(n [, t])      | A.quiet_load(n [, t])           | 
                           | A.quiet_load(s [, t])      | A.quiet_load(s [, t])           | 
                           
                           
### Inline function operations

Matlab                     | Armadillo C++              | ArmadilloJava                   | Notes
---------------------------|----------------------------|---------------------------------|------
                           | A.transform(f)             | A.transform(f)                  | **Note:** f is a function. 
                           | A.transform(l)             | A.transform(l)                  | *Not suppported* **Note:** l is a lambda function. 
                           | A.imbue(f)                 | A.imbue(f)                      | **Note:** f is a function. 
                           | A.imbue(l)                 |                                 | *Not suppported* **Note:** l is a lambda function. 
                           
                           
### C++ STL container functions

Matlab                     | Armadillo C++              | ArmadilloJava                   | Notes
---------------------------|----------------------------|---------------------------------|------
                           | A.clear()                  | A.clear()                       | 
                           | A.empty()                  | A.empty()                       | 
                           | A.size()                   | A.size()                        | 
                           
                           
### Miscellaneous
                           
Matlab                     | Armadillo C++              | ArmadilloJava                   | Notes
---------------------------|----------------------------|---------------------------------|------
                           | A.diag([k])                | A.diag([k])                     | 
                           | A.each_col([I])            | A.each_col([I])                 | 
                           | A.each_row([I])            | A.each_row([I])                 | 
                           | A.i([s])                   | A.i()                           | 
                           | A.t()                      | A.t()                           | 
                           | A.reset()                  | A.reset()                       | 
                           | A.copy_size(A)             | A.copy_size(A)                  | 
                           | A.eval()                   | A.eval()                        | *Not suppported*


Non-member functions
--------------------

### Maxtrix-valued element-wise functions

Matlab                     | Armadillo C++              | ArmadilloJava                   | Notes
---------------------------|----------------------------|---------------------------------|------
abs(A)                     | abs(A)                     | Arma.abs(A)                     | 
eps(A)                     | eps(A)                     | Arma.eps(A)                     | 
exp(A)                     | exp(A)                     | Arma.exp(A)                     | 
2 .^ A                     | exp2(A)                    | Arma.exp2(A)                    | 
10 .^ A                    | exp10(A)                   | Arma.exp10(A)                   | 
                           | trunc_exp(A)               | Arma.trunc_exp(A)               | **Matlab:** B = exp(A); B(isinf(B)) = realmax
log(A)                     | log(A)                     | Arma.log(A)                     | 
log2(A)                    | log2(A)                    | Arma.log2(A)                    | 
log10(A)                   | log10(A)                   | Arma.log10(A)                   | 
                           | trunc_log(A)               | Arma.trunc_log(A)               | **Matlab:** B = A; B(isinf(B)) = realmax; B(find(not(B))) = realmin; log(B)
A .^ p                     | pow(A, p)                  | Arma.pow(A, p)                  | 
sqrt(A)                    | sqrt(A)                    | Arma.sqrt(A)                    | 
A .^ 2                     | square(A)                  | Arma.square(A)                  | 
floor(A)                   | floor(A)                   | Arma.floor(A)                   | 
ceil(A)                    | ceil(A)                    | Arma.ceil(A)                    | 
round(A)                   | round(A)                   | Arma.round(A)                   | 
sign(A)                    | sign(A)                    | Arma.sign(A)                    | 

#### Trigonometric functions

Matlab                     | Armadillo C++              | ArmadilloJava                   | Notes
---------------------------|----------------------------|---------------------------------|------
sin(A)                     | sin(A)                     | Arma.sin(A)                     | 
asin(A)                    | asin(A)                    | Arma.asin(A)                    | 
asinh(A)                   | asinh(A)                   | Arma.asinh(A)                   | 
cos(A)                     | cos(A)                     | Arma.cos(A)                     | 
acos(A)                    | acos(A)                    | Arma.acos(A)                    | 
acosh(A)                   | acosh(A)                   | Arma.acosh(A)                   | 
tan(A)                     | tan(A)                     | Arma.tan(A)                     | 
atan(A)                    | atan(A)                    | Arma.atan(A)                    | 
atanh(A)                   | atanh(A)                   | Arma.atanh(A)                   | 


### Scalar-valued functions of vectors/matrices

Matlab                     | Armadillo C++              | ArmadilloJava                   | Notes
---------------------------|----------------------------|---------------------------------|------
sum(A(:))                  | accu(A)                    | Arma.accu(A)                    | 
                           | as_scalar(A)               | Arma.accu(A)                    | 
det(A)                     | det(A)                     | Arma.det(A)                     | 
dot(A, B)                  | dot(A, B)                  | Arma.dot(A, B)                  | **Note:** A, B are vectors.
                           | norm_dot(A, B)             | Arma.norm_dot(A, B)             | **Matlab:** dot(A, B) / sqrt(dot(A, A) * dot(B, B))
                           | log_det(v, s, A)           | Arma.log_det(v, s, A)           | **Matlab:** d = det(A); v = log(abs(d)); s = sign(d) (very inefficient)
norm(A, p)                 | norm(A, p)                 | Arma.norm(A, p)                 | 
rank(A [, t])              | rank(A [, t])              | Arma.rank(A [, t])              | **Default:** t = max(size(A))*eps(norm(A))
trace(A)                   | trace(A)                   | Arma.trace(A)                   | 


### Scalar/vector-valued functions of vectors/matrices

Matlab                     | Armadillo C++              | ArmadilloJava                   | Notes
---------------------------|----------------------------|---------------------------------|------
diag(A [, k])              | diagvec(A [, k])           | Arma.diagvec(A [, k])           | **Default:** k = 0 **Matlab:** Use A(k) if A is a vector.
prod(A [, d])              | prod(A [, d])              | Arma.prod(A [, d])              | **Note:** d is omitted if A is a vector. **Default:** d = 0
sum(A [, d])               | sum(A [, d])               | Arma.sum(A [, d])               | **Note:** d is omitted if A is a vector. **Default:** d = 0


#### Statistics
Matlab                     | Armadillo C++              | ArmadilloJava                   | Notes
---------------------------|----------------------------|---------------------------------|------
min(A [, d])               | min(A [, d])               | Arma.min(A [, d])               | **Note:** d is omitted if A is a vector. **Default:** d = 0
max(A [, d])               | max(A [, d])               | Arma.max(A [, d])               | **Note:** d is omitted if A is a vector. **Default:** d = 0
mean(A [, d])              | mean(A [, d])              | Arma.mean(A [, d])              | **Note:** d is omitted if A is a vector. **Default:** d = 0
median(A [, d])            | median(A [, d])            | Arma.median(A [, d])            | **Note:** d is omitted if A is a vector. **Default:** d = 0
std(A [, n] [, d])         | stddev(A [, n] [, d])      | Arma.stddev(A [, n] [, d])      | **Note:** d is omitted if A is a vector. **Default:** n = 0, d = 0
var(A [, n] [, d])         | var(A [, n] [, d])         | Arma.var(A [, n] [, d])         | **Note:** d is omitted if A is a vector. **Default:** n = 0, d = 0


### Matrix-valued functions of vectors/matrices

#### Matrix joins
Matlab                     | Armadillo C++              | ArmadilloJava                   | Notes
---------------------------|----------------------------|---------------------------------|------
[A B]                      | join_rows(A, B)            | Arma.join_rows(A, B)            | 
[A B]                      | join_horiz(A, B)           | Arma.join_horiz(A, B)           | 
[A; B]                     | join_cols(A, B)            | Arma.join_cols(A, B)            | 
[A; B]                     | join_vert(A, B)            | Arma.join_vert(A, B)            | 


#### Logic and relational operations

**ArmadilloJava:** Use R = Op.evaluate(A, Relation, B) to evaluate relational operations

Matlab                     | Armadillo C++              | ArmadilloJava                   | Notes
---------------------------|----------------------------|---------------------------------|------
all(R [, d])               | all(R [, d])               | Arma.all(R [, d])               | **Note:** R is a matrix or a relational expression **Default:** d = 0
any(R [, d])               | any(R [, d])               | Arma.any(R [, d])               | **Default:** d = 0
find(R [, k] [, s])        | find(R [, k] [, s])        | Arma.find(R [, k] [, s])        | **Default:** k = 0, s = "first"

                           
#### Sort functions
Matlab                     | Armadillo C++              | ArmadilloJava                   | Notes
---------------------------|----------------------------|---------------------------------|------
sort(A [, d] [, t])        | sort(A [, t] [, d])        | Arma.sort(A [, t] [, d])        | **Default:** t = 0 ('ascend' in Matlab), d = 0
[~, I] = sort(A [, t])     | sort_index(A [, t])        | Arma.sort_index(A [, t])        | **Note:** A is a vector. **Default:** t = 0 ('ascend' in Matlab)
[~, I] = sort(A [, t])     | stable_sort_index(A [, t]) | Arma.stable_sort_index(A [, t]) | **Note:** A is a vector. **Default:** t = 0 ('ascend' in Matlab)
                           

#### Reshape/resize
Matlab                     | Armadillo C++              | ArmadilloJava                   | Notes
---------------------------|----------------------------|---------------------------------|------ 
reshape(A, n, m)           | reshape(A, n, m [, d])     | Arma.reshape(A, n, m [, d])     | **Default:** d = 0
                           | resize(A, n, m)            | Arma.resize(A, n, m)            | 


#### Statistics

Matlab                     | Armadillo C++              | ArmadilloJava                   | Notes
---------------------------|----------------------------|---------------------------------|------
                           | cor(A [, B] [, t])         | Arma.cor(A [, B] [, t])         | **Default:** t = 0
cov(A [, B] [, t])         | cov(A [, B] [, t])         | Arma.cov(A [, B] [, t])         | **Default:** t = 0
                           
                           
#### Histogramm functions
Matlab                     | Armadillo C++              | ArmadilloJava                   | Notes
---------------------------|----------------------------|---------------------------------|------ 
                           | hist(X [, n [, d]])        | Arma.hist(X [, n [, d]])        | **Default:** n = 10, d = 0
                           | hist(X, c [, d])           | Arma.hist(X, c [, d])           | **Default:** d = 0
                           | histc(X, e [, d])          | Arma.histc(X, e [, d])          | **Default:** d = 0
                           

#### Swap functions

Matlab                     | Armadillo C++              | ArmadilloJava                   | Notes
---------------------------|----------------------------|---------------------------------|------
fliplr(A)                  | fliplr(A)                  | Arma.fliplr(A)                  | 
flipud(A)                  | flipud(A)                  | Arma.flipud(A)                  | 


#### Reinterpret functions

Matlab                     | Armadillo C++              | ArmadilloJava                   | Notes
---------------------------|----------------------------|---------------------------------|------
diag(A)                    | diagmat(A)                 | Arma.diagmat(A)                 | **Matlab:** Use diag(digag(A)) if A is not a vector.
triu(A)                    | trimatu(A)                 | Arma.trimatu(A)                 | 
tril(A)                    | trimatl(A)                 | Arma.trimatl(A)                 | 
                           | symmatu(A)                 | Arma.symmatu(A)                 | **Matlab:** B = triu(A); B + B' - diag(diag(A))
                           | symmatl(A)                 | Arma.symmatl(A)                 | **Matlab:** B = tril(A); B + B' - diag(diag(A))
                           
                    
#### Miscellaneous

Matlab                     | Armadillo C++              | ArmadilloJava                   | Notes
---------------------------|----------------------------|---------------------------------|------
A'                         | trans(A)                   | Arma.trans(A)                   | 
cumsum(A [, d])            | cumsum(A [, d])            | Arma.cumsum(A [, d])            | 
conv(A, B)                 | conv(A, B)                 | Arma.conv(A, B)                 | **Note:** A, B are vectors.
cross(A, B)                | cross(A, B)                | Arma.cross(A, B)                | **Note:** A, B are 3-dimensional vectors.
kron(A, B)                 | kron(A, B)                 | Arma.kron(A, B)                 | 
                           | shuffle(A [, d])           | Arma.shuffle(A [, d])           | **Default:** d = 0 **Matlab:** A(randperm(size(A, 1)), :) (for d = 0)
unique(A)                  | unique(A)                  | Arma.unique(A)                  | 
                           | vectorise(A [, d])         | Arma.vectorise(A [, d])         | **Default** d = 0 **Matlab:** Use A(:) for d = 0 and A(:)' otherwise.
                    
                    
### Decompositions

Matlab                     | Armadillo C++              | ArmadilloJava                   | Notes
---------------------------|----------------------------|---------------------------------|------
chol(X)                    | chol(X)                    | Arma.chol(X)                    | 
R = chol(X)                | chol(R, X)                 | Arma.chol(R, X)                 | 
eig(X)                     | eig_sym(X)                 | Arma.eig_sym(X)                 | 
V = eig(X)                 | eig_sym(V, X)              | Arma.eig_sym(V, X)              | 
[V, E] = eig(X)            | eig_sym(V, E, X [, m])     | Arma.eig_sym(V, E, X)           | **Matlab:** E = diag(E)
inv(A)                     | inv(A [, s])               | Arma.inv(A)                     | 
B = inv(A)                 | inv(B, A)                  | Arma.inv(B, A)                  | 
[L, U] = lu(X)             | lu(L, U, X)                | Arma.lu(L, U, X)                | 
[L, U, P] = lu(X)          | lu(L, U, P, X)             | Arma.lu(L, U, P, X)             | 
pinv(A [, t])              | pinv(A [, t])              | Arma.pinv(A [, t])              | **Default:** t = max(size(A))*norm(A)*eps
B = pinv(A [, t])          | pinv(B, A [, t])           | Arma.pinv(B, A [, t])           | **Default:** t = max(size(A))*norm(A)*eps
princomp(X)                | princomp(X)                | Arma.princomp(X)                | 
C = princomp(X)            | princomp(C, X)             | Arma.princomp(C, X)             | 
[C, S] = princomp(X)       | princomp(C, S, X)          | Arma.princomp(C, S, X)          | 
[C, S, L] = princomp(X)    | princomp(C, S, L, X)       | Arma.princomp(C, S, L, X)       | 
[C, S, L, T] = princomp(X) | princomp(C, S, L, T, X)    | Arma.princomp(C, S, L, T, X)    | 
[Q, R] = qr(X)             | qr(Q, R, X)                | Arma.qr(Q, R, X)                | 
[Q, R] = qr(A, 0)          | qr_econ(Q, R, X)           | Arma.qr_econ(Q, R, X)           | 
A \ B                      | solve(A, B [, s])          | Arma.solve(A, B)                | 
X = A \ B                  | solve(X, A, B [, s])       | Arma.solve(X, A, B)             | 
svd(X)                     | svd(X)                     | Arma.svd(X)                     | 
[~, S, ~] = svd(X)         | svd(S, X)                  | Arma.svd(S, X)                  | 
[U, S, V] = svd(X)         | svd(U, X, V, X [, m])      | Arma.svd(U, X, V, X)            | 
                           | svd_econ(U, X, V, X [, m]) | Arma.svd_econ(U, X, V, X)       | 
                           | syl(A, B, C)               | Arma.syl(A, B, C)               | **Matlab:** See [Matlab solution 1-35X8UD](http://www.mathworks.com/support/solutions/en/data/1-35X8UD/?product=ML&solution=1-35X8UD)
                           | syl(X, A, B, C)            | Arma.syl(X, A, B, C)            | **Matlab:** See [Matlab solution 1-35X8UD](http://www.mathworks.com/support/solutions/en/data/1-35X8UD/?product=ML&solution=1-35X8UD)

                     
Miscellaneous
-------------

Matlab                     | Armadillo C++              | ArmadilloJava                   | Notes
---------------------------|----------------------------|---------------------------------|------
isfinite(A)                | is_finite(A)               | Arma.is_finite(A)               | **Note:** A may be a matrix or a scalar

### Statistics 

**Armadillo C++:** running_stat<double> S

**ArmadilloJava:** RunningStat S

Matlab                     | Armadillo C++              | ArmadilloJava                   | Notes
---------------------------|----------------------------|---------------------------------|------
                           | S(v)                       | S.update(v)                     | 
                           | S.mean()                   | S.mean()                        | 
                           | S.var([n])                 | S.var([n])                      | **Default:** n = 0
                           | S.stddev([n])              | S.stddev([n])                   | **Default:** n = 0
                           | S.min()                    | S.min()                         | 
                           | S.max()                    | S.max()                         | 
                           | S.reset()                  | S.reset()                       | 
                           | S.count()                  | S.count()                       | 


**Armadillo C++:** running_stat_vec<double> S([c]) **Default:** c = true

**ArmadilloJava:** RunningStatVec S([c]) **Default:** c = true

Matlab                     | Armadillo C++              | ArmadilloJava                   | Notes
---------------------------|----------------------------|---------------------------------|------
                           | S(v)                       | S.update(v)                     | 
                           | S.mean()                   | S.mean()                        | 
                           | S.var([n])                 | S.var([n])                      | **Default:** n = 0
                           | S.stddev([n])              | S.stddev([n])                   | **Default:** n = 0
                           | S.min()                    | S.min()                         | 
                           | S.max()                    | S.max()                         | 
                           | S.cov([n])                 | S.cov([n])                      | **Note:** c must be true **Default:** n = 0
                           | S.reset()                  | S.reset()                       | 
                           | S.count()                  | S.count()                       | 

                      
### Wall clock

**Armadillo C++:** wall_clock W

**ArmadilloJava:** WallClock W

Matlab                     | Armadillo C++              | ArmadilloJava                   | Notes
---------------------------|----------------------------|---------------------------------|------
tic                        | W.tic()                    | W.tic()                         | 
toc                        | W.toc()                    | W.toc()                         | 
                       

### Pre-defined constants
Matlab                     | Armadillo C++              | ArmadilloJava                   | Notes
---------------------------|----------------------------|---------------------------------|------
pi                         | datum::pi                  | Datum.pi                        | 
Inf                        | datum::inf                 | Datum.inf                       | 
NaN                        | datum::nan                 | Datum.nan                       | 
exp(1)                     | datum::e                   | Datum.e                         | 
sqrt(2)                    | datum::sqrt2               | Datum.sqrt2                     | 
eps                        | datum::eps                 | Datum.eps                       | **Note:** Machine dependant
log(realmin)               | datum::log_min             | Datum.log_min                   | **Note:** Machine dependant
log(realmax)               | datum::log_max             | Datum.log_max                   | **Note:** Machine dependant
0.5772156649015329         | datum::euler               | Datum.euler                     | 
1.6180339887498948         | datum::gratio              | Datum.gratio                    | 
1.660538782e-27            | datum::m_u                 | Datum.m_u                       | 
6.02214179e23              | datum::N_A                 | Datum.N_A                       | 
1.3806504e-23              | datum::k                   | Datum.k                         | 
8.617343e-5                | datum::k_evk               | Datum.k_evk                     | 
0.52917720859e-10          | datum::a_0                 | Datum.a_0                       | 
927.400915e-26             | datum::mu_B                | Datum.mu_B                      | 
3.76730313461771e-2        | datum::Z_0                 | Datum.Z_0                       | 
7.7480917004e-5            | datum::G_0                 | Datum.G_0                       | 
8.9875517873681764e9       | datum::k_e                 | Datum.k_e                       | 
8.85418781762039e-12       | datum::eps_0               | Datum.eps_0                     | 
9.10938215e-31             | datum::m_e                 | Datum.m_e                       | 
1.602176487e-19            | datum::eV                  | Datum.eV                        | 
1.602176487e-19            | datum::ec                  | Datum.ec                        | 
96485.3399                 | datum::F                   | Datum.F                         | 
7.2973525376e-3            | datum::alpha               | Datum.alpha                     | 
137.035999679              | datum::alpha_inv           | Datum.alpha_inv                 | 
483597.891e9               | datum::K_J                 | Datum.K_J                       | 
1.25663706143592e-06       | datum::mu_0                | Datum.mu_0                      | 
2.067833667e-15            | datum::phi_0               | Datum.phi_0                     | 
8.314472                   | datum::R                   | Datum.R                         | 
6.67428e-11                | datum::G                   | Datum.G                         | 
6.62606896e-34             | datum::h                   | Datum.h                         | 
1.054571628e-34            | datum::h_bar               | Datum.h_bar                     | 
1.672621637e-27            | datum::m_p                 | Datum.m_p                       | 
10973731.568527            | datum::R_inf               | Datum.R_inf                     | 
299792458.0                | datum::c_0                 | Datum.c_0                       | 
5.670400e-8                | datum::sigma               | Datum.sigma                     | 
25812.807557               | datum::R_k                 | Datum.R_k                       | 
2.8977685e-3               | datum::b                   | Datum.b                         | 