(defun one-elem-list (e) (cons e nil))

(defun append-list (l1 l2)
  (if (null l1)
    l2
    (cons (car l1) (append-list (cdr l1) l2))))

(defun append-element (l e)
  (append-list l (one-elem-list e)))
     
(defun rev (l) 
  (if (null l) 
    l 
    (append-element (rev (cdr l)) 
                    (car l))))

(defun rev-s (ls k)
  (append-list (rev ls) k))

(defun nl-rev (l)
  (if (atom l)
    l
    (mapcar #'nl-rev(rev l))))

(defun rev-impl (l prl c)
  (if (or (null l) (funcall c l)) 
    (append-list prl l)
    (rev-impl (cdr l) (cons (car l) prl) c)))

(defun rev-g (l)
  (rev-impl l () 
  (lambda (ls) (equal nil (car ls)))))

(defun s-rev (l e)
  (rev-impl l ()
  (lambda (ls) (equal e (car ls)))))

(defun a-rev-impl (l prl e)
  (cond 
    ((null l) 
      (append-list prl l))
    ((equal e (car l)) 
      (append-list prl (cons (car l) (a-rev-impl (cdr l) () e))))
    (t 
      (a-rev-impl (cdr l) (cons (car l) prl) e))))

(defun a-rev (l e)
  (a-rev-impl l () e))

(defun f-rev (l e) 
  (cond 
    ((null l) l)
    ((equal e (car l)) (cons (car l) (rev-g (cdr l))))
    (t (cons (car l) (f-rev (cdr l) e)))))
