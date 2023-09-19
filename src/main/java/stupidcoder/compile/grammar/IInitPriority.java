package stupidcoder.compile.grammar;

public interface IInitPriority {
    /**
     * 向语法分析器构造器中注册符号的优先级关系
     * @param registry 注册器
     */
    void init(IPriorityRegistry registry);
}
