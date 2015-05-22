package ru.android_cnc.acnc.HAL.MotionController.StepPlan;

/**
 * Created by Sales on 22.05.2015.
 */
public class StepDerivatives {

    private double first_  = 0.0;
    private double second_ = 0.0;
    private double third_  = 0.0;

    public double getFirst() {
        return first_;
    }

    public double getSecond() {
        return second_;
    }

    public double getThird_() {
        return third_;
    }

    public void addMeasurement(double new_value){
        double old_first = getFirst();
        setFirst(new_value);
        if(old_first > 0.0){
            double old_second = getSecond();
            setSecond(old_first - getFirst());
            if(getSecond() > 0.0){
                setThird_(old_second - getSecond());
            }
        }
    }

    private void setFirst(double new_first) {
        this.first_ = Math.max(Math.abs(new_first), first_);
    }

    private void setSecond(double new_second) {
        this.second_ = Math.max(Math.abs(new_second), second_);
    }

    private void setThird_(double new_third) {
        this.third_ = Math.max(Math.abs(new_third), third_);
    }
}
