package org.juitar.flags;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author sha1n
 * Date: 12/2/13
 */
public class ScopedFlag extends Flag {

    private final Set<Scope> scopeSet;
    private final boolean inclusive;

    public ScopedFlag(String name) {
        this(name, Collections.synchronizedSet(new HashSet<Scope>()));
    }

    public ScopedFlag(String name, Set<Scope> scopeSet) {
        this(name, scopeSet, true);
    }

    public ScopedFlag(String name, Set<Scope> scopeSet, boolean inclusive) {
        super(name);
        this.scopeSet = scopeSet;
        this.inclusive = inclusive;
    }

    public ScopedFlag(String name, boolean inclusive) {
        this(name, Collections.synchronizedSet(new HashSet<Scope>()), inclusive);
    }

    public final boolean register(Scope scope) {
        return scopeSet.add(scope);
    }

    @Override
    public final boolean isOn(Scope scope) {
        boolean contains = scopeSet.contains(scope);
        return inclusive == contains; // inclusive & contains -> true, exclusive & !contains -> true
    }

    @Override
    public final boolean isOn() {
        return false;
    }

    @Override
    public final boolean isScoped() {
        return true;
    }


}