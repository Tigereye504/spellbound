package net.tigereye.spellbound.interfaces;

public abstract class DelayedAction {
    protected int timeToAct = 1;
    public void setTicks(int ticks){timeToAct = ticks;}
    public int getTicks(){return timeToAct;}
    public boolean actOrDecrementTicks(){
        timeToAct--;
        if(timeToAct <= 0){
            act();
            return false;
        }
        return true;
    }
    abstract public void act();
}
