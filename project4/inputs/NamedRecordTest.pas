PROGRAM NamedRecordTest;

TYPE
    String2  = PACKED ARRAY [0..1 ] OF char;
    String5  = PACKED ARRAY [0..4 ] OF char;
    String8  = PACKED ARRAY [0..7 ] OF char;
    String16 = PACKED ARRAY [0..15] OF char;

    AddressRec = RECORD
                     street : String16;
                     city   : String16;
                     state  : String2;
                     zip    : String5;
                 END;

    PersonRec =  RECORD
                     firstName : String16;
                     lastName  : String16;
                     age       : integer;
                     address   : AddressRec;
                     phones    : ARRAY [0..1] OF String8;
                 END;

VAR
    i : integer;
    
    john : PersonRec;
    mary : PersonRec;
    team : ARRAY [1..4] OF PersonRec;

FUNCTION print(VAR person : PersonRec) : integer;

    VAR
        age  : integer;
        addr : AddressRec;
        phs  : ARRAY [0..1] OF String8;

    BEGIN
        age := person.age;

        addr.street := person.address.street;
        addr.city   := person.address.city;
        addr.state  := person.address.state;
        addr.zip    := person.address.zip;

        phs[0] := person.phones[0];
        phs[1] := person.phones[1];

        writeln;
        writeln('First name: ', person.firstName);
        writeln(' Last name: ', person.lastName);
        writeln('       Age: ', age);
        writeln('    Street: ', addr.street);
        writeln('      City: ', addr.city);
        writeln('     State: ', addr.state);
        writeln('       ZIP: ', addr.zip);
        writeln('  Phone #1: ', phs[0]);
        writeln('  Phone #2: ', phs[1]);
    END;

BEGIN
    john.firstName := 'John';
    john.lastName  := 'Doe';
    john.age := 24;
    john.address.street := '1680 25th Street';
    john.address.city   := 'San Pablo';
    john.address.state  := 'CALIFORNIA';
    john.address.zip    := '94806';
    john.phones[0]      := '111-1111';
    john.phones[1]      := '222-2222';
    
    i := print(john);

    mary.firstName := 'Mary';
    mary.lastName  := 'Jane';
    mary.age := 22;
    mary.address.street := '4899 Bela Drive ';
    mary.address.city   := 'San Jose';
    mary.address.state  := 'CALIFORNIA';
    mary.address.zip    := '95129';
    mary.phones[0]      := '333-3333';
    mary.phones[1]      := '444-4444';
    
    i := print(mary);
    
    team[3].firstName      := mary.firstName;
    team[3].lastName       := mary.lastName;
    team[3].age            := mary.age;
    team[3].address.street := mary.address.street;
    team[3].address.city   := mary.address.city;
    team[3].address.state  := mary.address.state;
    team[3].address.zip    := mary.address.zip;
    team[3].phones[0]      := mary.phones[0];
    team[3].phones[1]      := mary.phones[1];
    
    i := print(team[3]);
END.
