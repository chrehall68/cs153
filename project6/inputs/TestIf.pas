PROGRAM TestIf;

VAR
    b, p, q : boolean;
    
BEGIN
    b := true;
    IF b THEN writeln('1.  THEN b is true');
    
    b := false;
    IF b THEN writeln('2.  THEN b is true');
    
    if b THEN BEGIN
        writeln('3a. THEN b = ', b);
        writeln('3b. THEN b is true');
    END
    ELSE BEGIN
        writeln('3c. ELSE b = ', b);
        writeln('3d. ELSE b is false');
    END;
                       
    p := true;
    q := false;
    
    IF p THEN IF q THEN writeln('4a. THEN p = ', p, ' and q = ', q)
                   ELSE writeln('4b. ELSE p = ', p, ' and q = ', q);
    
    IF p THEN BEGIN 
                  IF q THEN writeln('5a. THEN p = ', p, ' and q = ', q)
                       ELSE writeln('5b. ELSE p = ', p, ' and q = ', q);
         END;
    
    IF p THEN BEGIN 
                  IF q THEN writeln('6a. THEN p = ', p, ' and q = ', q)
              END
         ELSE writeln(' 6b. ELSE p = ', p, ' and q = ', q);

    IF b THEN writeln('7a. THEN b = ', b)
         ELSE IF q THEN writeln('7b. THEN q = ', q)
                   ELSE IF p THEN writeln('7c. THEN p = ', p)
                             ELSE writeln('7d. ELSE p = ', p);
END.
