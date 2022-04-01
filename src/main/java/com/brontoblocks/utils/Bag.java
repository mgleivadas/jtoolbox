package com.brontoblocks.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.function.Predicate;

import static java.util.Collections.unmodifiableCollection;

public class Bag<T> {

    public static StringBag stringBagBackedByList() {
        return new StringBag(new ArrayList<>());
    }

    public static class StringBag extends Bag<String> {
        private StringBag(Collection<String> bag) {
            super(bag);
        }
    }

    public static <T> Bag<T> newBackedByList() {
        return new Bag<>(new ArrayList<>());
    }

    public static <T> Bag<T> newBackedBySet() {
        return new Bag<>(new HashSet<>());
    }

    public boolean isEmpty() {
        return bag.isEmpty();
    }

    public Bag<T> addIfTrue(boolean condition, T element) {
        if (condition) {
            bag.add(element);
        }
        return this;
    }

    public Bag<T> addIfFalse(boolean condition, T element) {
        if (!condition) {
            bag.add(element);
        }
        return this;
    }

    public Bag<T> addIfBoth(boolean conditionOne, boolean conditionTwo, T element) {
        if (conditionOne && conditionTwo) {
            bag.add(element);
        }
        return this;
    }

    public Bag<T> addIfNone(boolean conditionOne, boolean conditionTwo, T element) {
        if (!conditionOne && !conditionTwo) {
            bag.add(element);
        }
        return this;
    }

    public <U> Bag<T> addIfNullOrTrue(U inputElement, Predicate<U> condition, T element) {
        if (inputElement == null || condition.test(inputElement)) {
            bag.add(element);
        }
        return this;
    }

    public <U> Bag<T> addIfNullOrFalse(U inputElement, Predicate<U> condition, T element) {
        if (inputElement == null || condition.negate().test(inputElement)) {
            bag.add(element);
        }
        return this;
    }

    public Bag<T> addIfEither(boolean conditionOne, boolean conditionTwo, T element) {
        if (conditionOne || conditionTwo) {
            bag.add(element);
        }
        return this;
    }

    public Bag<T> addIfNotANumber(String number, T element) {
        boolean isANumber = StringUtils.splitBy(number, 1).stream().allMatch(i -> {
            try {
                Long.valueOf(i);
                return true;
            } catch (Throwable ex) {
                return false;
            }
        });

        if (!isANumber) {
            bag.add(element);
        }
        return this;
    }

    public Collection<T> getElements() {
        return unmodifiableCollection(bag);
    }

    public void clearBag() {
         bag.clear();
    }

    private Bag(Collection<T> bag) {
        this.bag = bag;
    }

    private final Collection<T> bag;
}
