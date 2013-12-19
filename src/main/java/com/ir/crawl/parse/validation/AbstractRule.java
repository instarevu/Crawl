package com.ir.crawl.parse.validation;


public abstract class AbstractRule implements Rule {

    RuleType ruleType;

    AbstractRule(RuleType ruleType){
        this.ruleType = ruleType;
    }

    public RuleType getRuleType(){
        return ruleType;
    }

}
