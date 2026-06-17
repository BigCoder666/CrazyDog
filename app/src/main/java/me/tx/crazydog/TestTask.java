package me.tx.crazydog;

import android.util.Log;

import me.tx.crazydog.task.TaskDog;

public class TestTask extends TaskDog<Boolean> {
    @Override
    public Boolean taskDetail() {
        return test();
    }

    private boolean test(){
        for(int i= 0;i<10;i++){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
        }
        return true;
    }
}
