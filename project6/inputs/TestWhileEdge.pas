program TestWhileEdge;

var
    n, visits : integer;

begin
    n := 5;
    visits := 0;

    { A false condition must skip its body. }
    while n < 5 do visits := visits + 100;
    writeln('skipped = ', visits);

    { The condition must be reevaluated after every iteration. }
    while n > 0 do begin
        n := n - 1;
        visits := visits + 1
    end;
    writeln('ran = ', visits);

    { A constant false condition must also skip its body. }
    while false do visits := visits + 100;
    writeln('final = ', visits)
end.
