package com.rnkrsoft.gitserver.log.impl;


import com.rnkrsoft.gitserver.log.AbstractLogger;
import com.rnkrsoft.gitserver.utils.MessageFormatter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class AndroidLogger extends AbstractLogger {
    Method d;
    Method d1;
    Method i;
    Method i1;
    Method w;
    Method w1;
    Method e;
    Method e1;

    public AndroidLogger() {
        try {
            Class clazz = Class.forName("android.util.Log");
            this.d = clazz.getMethod("d", new Class[]{String.class, String.class});
            this.d1 = clazz.getMethod("d", new Class[]{String.class, String.class, Throwable.class});
            this.i = clazz.getMethod("i", new Class[]{String.class, String.class});
            this.i1 = clazz.getMethod("i", new Class[]{String.class, String.class, Throwable.class});
            this.w = clazz.getMethod("w", new Class[]{String.class, String.class});
            this.w1 = clazz.getMethod("w", new Class[]{String.class, String.class, Throwable.class});
            this.e = clazz.getMethod("e", new Class[]{String.class, String.class});
            this.e1 = clazz.getMethod("e", new Class[]{String.class, String.class, Throwable.class});
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (NoSuchMethodException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void debug(String format, Throwable cause, Object... args) {
        String caller = getCaller();
        String msg = MessageFormatter.format(format, args);
        try {
            d1.invoke(null, caller, msg, cause);
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (InvocationTargetException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void debug(String format, Object[] args) {
        String caller = getCaller();
        String msg = MessageFormatter.format(format, args);
        try {
            d.invoke(null, caller, msg);
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (InvocationTargetException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void info(String format, Throwable cause, Object... args) {
        String caller = getCaller();
        String msg = MessageFormatter.format(format, args);
        try {
            i1.invoke(null, caller, msg, cause);
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (InvocationTargetException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void info(String format, Object[] args) {
        String caller = getCaller();
        String msg = MessageFormatter.format(format, args);
        try {
            i.invoke(null, caller, msg);
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (InvocationTargetException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void warn(String format, Throwable cause, Object... args) {
        String caller = getCaller();
        String msg = MessageFormatter.format(format, args);
        try {
            w.invoke(null, caller, msg, cause);
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (InvocationTargetException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void warn(String format, Object[] args) {
        String caller = getCaller();
        String msg = MessageFormatter.format(format, args);
        try {
            w1.invoke(null, caller, msg);
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (InvocationTargetException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void error(String format, Throwable cause, Object... args) {
        String caller = getCaller();
        String msg = MessageFormatter.format(format, args);
        try {
            e1.invoke(null, caller, msg, cause);
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (InvocationTargetException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void error(String format, Object[] args) {
        String caller = getCaller();
        String msg = MessageFormatter.format(format, args);
        try {
            e.invoke(null, caller, msg);
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (InvocationTargetException ex) {
            ex.printStackTrace();
        }
    }
}
