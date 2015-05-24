package com.lynnlyc.bridge;

import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.InvokeStmt;

/**
 * Created by yuanchun on 5/17/15.
 * Package: webview-flow
 */
public class BridgeContext {
    private SootMethod method;
    private Unit unit;
    private int unitId;
    public BridgeContext(SootMethod method, Unit unit, int unitId) {
        this.unit = unit;
        this.unitId = unitId;
        this.method = method;
    }

    public String toString() {
        return String.format("{method}%s, {unit%d}%s", method, unitId, unit);
    }
}