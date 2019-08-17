/*
 * Radon - An open-source Java obfuscator
 * Copyright (C) 2019 ItzSomebody
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package me.itzsomebody.radon.transformers.obfuscators.virtualizer;

public interface VMOpcodes {
    int VM_NULL_PUSH = 0;
    int VM_INT_PUSH = 1;
    int VM_LONG_PUSH = 2;
    int VM_FLOAT_PUSH = 3;
    int VM_DOUBLE_PUSH = 4;
    int VM_OBJ_PUSH = 5;
    int VM_ADD = 6;
    int VM_SUB = 7;
    int VM_MUL = 8;
    int VM_DIV = 9;
    int VM_MOD = 10;
    int VM_AND = 11;
    int VM_OR = 12;
    int VM_XOR = 13;
    int VM_SHL = 14;
    int VM_SHR = 15;
    int VM_USHR = 16;
    int VM_LOAD = 17;
    int VM_STORE = 18;
    int VM_ARR_LOAD = 19;
    int VM_ARR_STORE = 20;
    int VM_POP = 21;
    int VM_POP2 = 22;
    int VM_DUP = 23;
    int VM_SWAP = 24;
    int VM_INC = 25;
    int VM_PRIM_CAST = 26; // Primrose Everdeen!
    int VM_LCMP = 27;
    int VM_FCMPL = 28;
    int VM_FCMPG = 29;
    int VM_DCMPL = 30;
    int VM_DCMPG = 31;
    int VM_JZ = 32;
    int VM_JNZ = 33;
    int VM_JLT = 34;
    int VM_JLE = 35;
    int VM_JGT = 36;
    int VM_JGE = 37;
    int VM_JEQ = 38;
    int VM_JMP = 39;
    int VM_JSR = 40;
    int VM_RET = 41;
    int VM_VIRT_GET = 42;
    int VM_STATIC_GET = 43;
    int VM_VIRT_SET = 44;
    int VM_STATIC_SET = 45;
    int VM_VIRT_CALL = 46;
    int VM_STATIC_CALL = 47;
    int VM_INSTANTIATE = 48;
    int VM_NEW_ARR = 49;
    int VM_ARR_LENGTH = 50;
    int VM_THROW = 51;
    int VM_CHECKCAST = 52;
    int VM_INSTANCE_OF = 53;
    int VM_MONITOR = 54;
    int VM_JN = 55;
    int VM_JNN = 56;
    int VM_NOP = 57;
    int VM_KILL = 58;
    int VM_NEG = 59;
    int VM_JNE = 60;
}
