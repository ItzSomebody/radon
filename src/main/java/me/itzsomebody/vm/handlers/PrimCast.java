package me.itzsomebody.vm.handlers;

import me.itzsomebody.vm.VM;
import me.itzsomebody.vm.datatypes.JDouble;
import me.itzsomebody.vm.datatypes.JFloat;
import me.itzsomebody.vm.datatypes.JInteger;
import me.itzsomebody.vm.datatypes.JLong;
import me.itzsomebody.vm.datatypes.JTop;

public class PrimCast extends Handler {
    @Override
    public void handle(VM vm, Object[] operands) {
        int indicator = (Integer) operands[0];

        switch (indicator) {
            case 0: // I2L
                vm.push(new JLong((long) vm.pop().asInt()));
                vm.push(JTop.getTop());
                break;
            case 1: // I2F
                vm.push(new JFloat((float) vm.pop().asInt()));
                break;
            case 2:  // I2D
                vm.push(new JDouble((double) vm.pop().asInt()));
                vm.push(JTop.getTop());
                break;
            case 3: // L2I
                vm.pop();
                vm.push(new JInteger((int) vm.pop().asLong()));
                break;
            case 4: // L2F
                vm.pop();
                vm.push(new JFloat((float) vm.pop().asLong()));
                break;
            case 5: // L2D
                vm.pop();
                vm.push(new JDouble((double) vm.pop().asLong()));
                vm.push(JTop.getTop());
            case 6: // F2I
                vm.push(new JInteger((int) vm.pop().asFloat()));
                break;
            case 7: // F2L
                vm.push(new JLong((long) vm.pop().asFloat()));
                break;
            case 8: // F2D
                vm.push(new JDouble((double) vm.pop().asFloat()));
                vm.push(JTop.getTop());
                break;
            case 9: // D2I
                vm.pop();
                vm.push(new JInteger((int) vm.pop().asDouble()));
                break;
            case 10: // D2L
                vm.pop();
                vm.push(new JLong((long) vm.pop().asDouble()));
                vm.push(JTop.getTop());
                break;
            case 11: // D2F
                vm.pop();
                vm.push(new JFloat((float) vm.pop().asDouble()));
                break;
            case 12: // I2B
                vm.push(new JInteger((byte) vm.pop().asInt()));
                break;
            case 13: // I2C
                vm.push(new JInteger((char) vm.pop().asInt()));
                break;
            case 14: // I2S
                vm.push(new JInteger((short) vm.pop().asInt()));
                break;
        }
    }
}
