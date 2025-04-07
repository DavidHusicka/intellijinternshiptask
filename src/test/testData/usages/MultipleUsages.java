public class MultipleUsages {
    void foo() {
        bar();
    }

    void bar() {
        baz();
    }

    void biz() {
        interestingMethod();
    }

    void baz() {
        biz();
        interestingMethod();
    }
}