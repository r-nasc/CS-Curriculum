#lang racket
(provide (all-defined-out))

(define (sequence low high stride)
  (if (> low high)
    null
    (cons low (sequence (+ low stride) high stride))))

(define (string-append-map xs suffix)
  (map (lambda (x) (string-append x suffix)) xs))

(define (list-nth-mod xs n)
  (cond [(< n 0) (error "list-nth-mod: negative number")]
        [(null? xs) (error "list-nth-mod: empty list")]
        [#t (car (list-tail xs (remainder n (length xs))))]))

(define (stream-for-n-steps s n)
  (if (= n 0)
    null
    (let ([pr (s)])
         (cons (car pr) (stream-for-n-steps (cdr pr) (- n 1))))))

(define funny-number-stream
  (letrec ([f (lambda (x)
          (if (= (remainder x 5) 0)
            (cons (- 0 x) (lambda () (f (+ x 1))))
            (cons x (lambda () (f (+ x 1))))))])
  (lambda () (f 1))))

(define dan-then-dog
  (letrec ([dan (lambda () (cons "dan.jpg" dog))]
           [dog (lambda () (cons "dog.jpg" dan))])
  dan))

(define (stream-add-zero s)
  (letrec ([f (lambda (x) (cons (cons 0 (car x)) (lambda ()(f ((cdr x))))))])
  (lambda () (f (s)))))

(define (cycle-lists xs ys)
  (letrec ([f (lambda (n) (cons (cons (list-nth-mod xs n) (list-nth-mod ys n)) (lambda () (f (+ n 1)))))])
  (lambda () (f 0))))

(define (vector-assoc v vec)
  (letrec ([len (vector-length vec)]
           [f (lambda (n)
                (if (= len n) #f
                    (let ([nv (vector-ref vec n)])
                      (if (and (pair? nv) (= (car nv) v))
                          nv
                          (f (+ n 1))))))])
  (f 0)))

(define (cached-assoc xs n)
  (letrec([memo (make-vector n #f)]
          [position 0]
          [f (lambda (v)
               (let ([ans (vector-assoc v memo)])
                 (if ans
                     ans
                     (let ([new-ans (assoc v xs)])
                       (if new-ans
                           (begin (vector-set! memo position new-ans)
                                  (set! position (remainder (+ position 1) n))
                                  new-ans) #f)))))])
  f))

(define-syntax while-less
  (syntax-rules (do)
    [(while-less e1 do e2)
     (begin (letrec ([e e1] [f (lambda() (if (> e e2) (f) #t ))])
            (f)))]))
