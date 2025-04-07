public class SingleUsage {
    void foo() {
        bar();
    }

    void bar() {
        baz();
    }

    void baz() {
        interestingMethod();
    }
}