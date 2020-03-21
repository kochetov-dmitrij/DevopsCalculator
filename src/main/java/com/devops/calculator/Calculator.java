/*
 * Copyright 2010 David Green
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.devops.calculator;

import java.math.BigInteger;

public class Calculator {

    private static final BigInteger TEN = new BigInteger("10");
    private static final BigInteger ZERO = new BigInteger("0");

    private BigInteger op1 = ZERO;
    private BigInteger op2 = null;
    private Operation operation = null;

    private String lastRecord = null;

    /**
     * Appends a digit - what happens when a user presses a number key.
     */
    public void digit(int digit) {
        if ((digit < 0) || (digit > 9)) {
            throw new IllegalArgumentException("Invalid digit: " + digit);
        }
        if (op2 != null) {
            op2 = op2.multiply(TEN).add(BigInteger.valueOf(digit));
        } else {
            op2 = BigInteger.valueOf(digit);
        }
        lastRecord = null;
    }

    public void add() {
        setOperation(new AddOperation());
    }

    public void subtract() {
        setOperation(new SubtractOperation());
    }

    public void multiply() {
        setOperation(new MultiplyOperation());
    }

    public void divide() {
        setOperation(new DivideOperation());
    }

    /**
     * Sets the operation we're working on.
     * If there is already an operation and a previous number (e.g. we have 2 + 2 _)
     * then calculate the first operation first and stash the result.
     * <p>
     * Because operations are calculated left first operator precedence is not honoured.
     * E.g. 2 + 3 * 2
     * Returns 10 not 8
     * <p>
     * I.e. process as (2+3)*2 not 2+(3*2)
     */
    private void setOperation(Operation operation) {
        // If we have an operation (e.g. 1+2) calculate it first
        equals();
        this.operation = operation;
    }

    /**
     * Calculates the current operation - what happens when the user presses =
     */
    public void equals() {
        if (operation != null) {
            BigInteger a = op1;
            BigInteger b = op2 != null ? op2 : op1;
            op1 = operation.apply(a, b);
            lastRecord = String.format("%d %c %d = %d", a, operation.getSign(), b, op1);
        } else if (op2 != null) {
            // Stash the currently edited number, so it's no longer editable
            op1 = op2;
            lastRecord = null;
        } else {
            lastRecord = null;
        }
        op2 = null;
        operation = null;
    }

    /**
     * Reset the calculator
     */
    public void clear() {
        lastRecord = null;
        op2 = null;
        op1 = ZERO;
        operation = null;
    }

    /**
     * @return The amount to display on the calculator screen
     */
    public String getDisplayAmount() {
        if (op2 == null) {
            return op1.toString();
        }
        return op2.toString();
    }

    public String getLastRecord() {
        return lastRecord;
    }
}
