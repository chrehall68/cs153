public class NamedRecordTest {
    private static class AddressRec {

        String street;
        String city;
        String state;
        String zip;
    }

    private static class PersonRec {
        String firstName;
        String lastName;
        int age;
        AddressRec address = new AddressRec();
        String phones[] = new String[2];
    }

    private static int oThER;
    private static PersonRec jOhn = new PersonRec();
    private static PersonRec mary = new PersonRec();
    private static PersonRec tEaM[] = {
        new PersonRec(), new PersonRec(), new PersonRec(), new PersonRec()
    };
    private static PersonRec nestedArr[][][][] = {
        {
            {
                {
                    new PersonRec(),
                    new PersonRec(),
                    new PersonRec(),
                    new PersonRec(),
                    new PersonRec()
                },
                {
                    new PersonRec(),
                    new PersonRec(),
                    new PersonRec(),
                    new PersonRec(),
                    new PersonRec()
                },
                {
                    new PersonRec(),
                    new PersonRec(),
                    new PersonRec(),
                    new PersonRec(),
                    new PersonRec()
                },
                {
                    new PersonRec(),
                    new PersonRec(),
                    new PersonRec(),
                    new PersonRec(),
                    new PersonRec()
                }
            },
            {
                {
                    new PersonRec(),
                    new PersonRec(),
                    new PersonRec(),
                    new PersonRec(),
                    new PersonRec()
                },
                {
                    new PersonRec(),
                    new PersonRec(),
                    new PersonRec(),
                    new PersonRec(),
                    new PersonRec()
                },
                {
                    new PersonRec(),
                    new PersonRec(),
                    new PersonRec(),
                    new PersonRec(),
                    new PersonRec()
                },
                {
                    new PersonRec(),
                    new PersonRec(),
                    new PersonRec(),
                    new PersonRec(),
                    new PersonRec()
                }
            },
            {
                {
                    new PersonRec(),
                    new PersonRec(),
                    new PersonRec(),
                    new PersonRec(),
                    new PersonRec()
                },
                {
                    new PersonRec(),
                    new PersonRec(),
                    new PersonRec(),
                    new PersonRec(),
                    new PersonRec()
                },
                {
                    new PersonRec(),
                    new PersonRec(),
                    new PersonRec(),
                    new PersonRec(),
                    new PersonRec()
                },
                {
                    new PersonRec(),
                    new PersonRec(),
                    new PersonRec(),
                    new PersonRec(),
                    new PersonRec()
                }
            }
        },
        {
            {
                {
                    new PersonRec(),
                    new PersonRec(),
                    new PersonRec(),
                    new PersonRec(),
                    new PersonRec()
                },
                {
                    new PersonRec(),
                    new PersonRec(),
                    new PersonRec(),
                    new PersonRec(),
                    new PersonRec()
                },
                {
                    new PersonRec(),
                    new PersonRec(),
                    new PersonRec(),
                    new PersonRec(),
                    new PersonRec()
                },
                {
                    new PersonRec(),
                    new PersonRec(),
                    new PersonRec(),
                    new PersonRec(),
                    new PersonRec()
                }
            },
            {
                {
                    new PersonRec(),
                    new PersonRec(),
                    new PersonRec(),
                    new PersonRec(),
                    new PersonRec()
                },
                {
                    new PersonRec(),
                    new PersonRec(),
                    new PersonRec(),
                    new PersonRec(),
                    new PersonRec()
                },
                {
                    new PersonRec(),
                    new PersonRec(),
                    new PersonRec(),
                    new PersonRec(),
                    new PersonRec()
                },
                {
                    new PersonRec(),
                    new PersonRec(),
                    new PersonRec(),
                    new PersonRec(),
                    new PersonRec()
                }
            },
            {
                {
                    new PersonRec(),
                    new PersonRec(),
                    new PersonRec(),
                    new PersonRec(),
                    new PersonRec()
                },
                {
                    new PersonRec(),
                    new PersonRec(),
                    new PersonRec(),
                    new PersonRec(),
                    new PersonRec()
                },
                {
                    new PersonRec(),
                    new PersonRec(),
                    new PersonRec(),
                    new PersonRec(),
                    new PersonRec()
                },
                {
                    new PersonRec(),
                    new PersonRec(),
                    new PersonRec(),
                    new PersonRec(),
                    new PersonRec()
                }
            }
        }
    };

    static void print(PersonRec person) {
        int age;
        AddressRec addr = new AddressRec();
        String phs[] = new String[2];
        age = person.age;
        addr.street = person.address.street;
        addr.city = person.address.city;
        addr.state = person.address.state;
        addr.zip = person.address.zip;
        phs[0] = person.phones[0];
        phs[1] = person.phones[1];
        System.out.println();
        System.out.printf("First name: %s\n", person.firstName);
        System.out.printf(" Last name: %s\n", person.lastName);
        System.out.printf("       Age: %d\n", age);
        System.out.printf("    Street: %s\n", addr.street);
        System.out.printf("      City: %s\n", addr.city);
        System.out.printf("     State: %s\n", addr.state);
        System.out.printf("       ZIP: %s\n", addr.zip);
        System.out.printf("  Phone #1: %s\n", phs[0]);
        System.out.printf("  Phone #2: %s\n", phs[1]);
    }

    public static void main(String[] args) {
        java.time.Instant _start = java.time.Instant.now();

        oThER = 3;
        oThER = 4;
        jOhn.firstName = "John";
        jOhn.lastName = "Doe";
        jOhn.age = 24;
        jOhn.address.street = "1680 25th Street";
        jOhn.address.city = "San Pablo";
        jOhn.address.state = "CALIFORNIA";
        jOhn.address.zip = "94806";
        jOhn.phones[0] = "111-1111";
        jOhn.phones[1] = "222-2222";
        print(jOhn);
        mary.firstName = "Mary";
        mary.lastName = "Jane";
        mary.age = 22;
        mary.address.street = "4899 Bela Drive ";
        mary.address.city = "San Jose";
        mary.address.state = "CALIFORNIA";
        mary.address.zip = "95129";
        mary.phones[0] = "333-3333";
        mary.phones[1] = "444-4444";
        print(mary);
        tEaM[(3) - 1].firstName = mary.firstName;
        tEaM[(3) - 1].lastName = mary.lastName;
        tEaM[(3) - 1].age = mary.age;
        tEaM[(3) - 1].address.street = mary.address.street;
        tEaM[(3) - 1].address.city = mary.address.city;
        tEaM[(3) - 1].address.state = mary.address.state;
        tEaM[(3) - 1].address.zip = mary.address.zip;
        tEaM[(3) - 1].phones[0] = mary.phones[0];
        tEaM[(3) - 1].phones[1] = mary.phones[1];
        print(tEaM[(3) - 1]);

        java.time.Instant _end = java.time.Instant.now();
        long _elapsed = java.time.Duration.between(_start, _end).toMillis();
        System.out.printf("\n[%,d milliseconds execution time.]\n", _elapsed);
    }
}
