package com.rnkrsoft.orm.session;

import com.rnkrsoft.orm.Orm;

public class SessionFactory {
    /**
     * 所有的数据库会话
     */
    final static ThreadLocal<Session> SESSIONS = new ThreadLocal<Session>();

    public static Session createSession(Orm orm) {
        Session session = SESSIONS.get();
        if (session != null) {
            return session;
        }
        session = new DefaultSession(orm);
        SESSIONS.set(session);
        return session;
    }

    public static Session getSession() {
        Session session = SESSIONS.get();
        if (session != null) {
            return session;
        }
        return null;
    }

    public static void close() {
        SESSIONS.set(null);
    }
}
