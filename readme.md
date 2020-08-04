# byte-buddy-code

1. Code [Main](/src/main/java/Main.java) is about how to advise a method after this method has been intercepted.
Watch more on [this](https://github.com/raphw/byte-buddy/issues/900).
2. Code [MainForCircularity](src/main/java/MainForCircularity.java) is about class loading circularity.
Watch more on [this](https://github.com/raphw/byte-buddy/issues/530#issuecomment-419002236),
but it can only fix the two-level of inner class like `Bike$Wheel`. If you want to fix multi-level of inner class 
like `Bike$Wheel$WheelInner`, you should call empty advice one more time. 
There must be a more effective and elegant way to fix this.
3. Code [Main](/src/main/java/MainForParaAttachTemplateCallAdvisor.java) is about how to use dynamic local variable in advice method enter and exit 
by redefine advice class and load it in app class-loader.

