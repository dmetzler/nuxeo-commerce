package org.nuxeo.commerce.core;

import org.nuxeo.ecm.core.api.CoreInstance;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.core.api.repository.RepositoryManager;
import org.nuxeo.runtime.api.Framework;

/**
 * Handler of the sessions
 *
 */
public final class SessionHandler implements AutoCloseable {

    private static ThreadLocal<SessionHandler> holder = new ThreadLocal<SessionHandler>();

    private SessionHandler existing = null;

    private CoreSession session;

    private SessionProvider sessionFactory;

    private boolean mustClose = true;

    private SessionHandler() {
        super();
        this.existing = SessionHandler.holder.get();
        SessionHandler.holder.set(this);
    }

    private SessionHandler(SessionProvider sessionFactory) {
        this();
        this.sessionFactory = sessionFactory;
    }

    private SessionHandler(CoreSession session, boolean mustClose) {
        this();
        this.session = session;
        this.mustClose = mustClose;
    }

    private CoreSession internalGetSession() {
        if (session == null && sessionFactory != null) {
            session = sessionFactory.getSession();
        }
        return session;
    }

    /**
     * Get the current session from the local thread.
     *
     * @return The current Studio session
     * @throws NoSessionException
     */
    public static CoreSession getSession() throws NoSessionException {
        SessionHandler sessionHandler = holder.get();
        if (sessionHandler == null) {
            throw new NoSessionException();
        }
        CoreSession session = sessionHandler.internalGetSession();
        if (session == null) {
            throw new NoSessionException();
        }
        return session;
    }

    public static SessionHandler attach(CoreSession session) {
        SessionHandler handler = new SessionHandler(session, false);
        return handler;
    }

    /**
     * It opens a session for the given principal. It he's already logged in, it keeps the existing {@link CoreSession}.
     * Otherwise, it provides a new one. If no session was created, no session will be close by the closable method,
     * otherwise, it will close the session.
     *
     * @param principal The principal for which the session must be created.
     * @return A closable object.
     */
    public static SessionHandler open(NuxeoPrincipal principal) {
        boolean mustClose = true;
        SessionHandler handler = holder.get();
        CoreSession session = null;
        if (handler != null && handler.session != null && principal.equals(handler.session.getPrincipal())) {
            // try to use the existing session if the same principal is used
            session = handler.session;
            mustClose = false;
        } else {
            // otherwise create a new one
            RepositoryManager repositoryManager = Framework.getService(RepositoryManager.class);
            String repositoryName = repositoryManager.getDefaultRepositoryName();
            session = CoreInstance.openCoreSession(repositoryName, principal);
        }
        return new SessionHandler(session, mustClose);
    }

    /**
     * It provides a session for the given principal but it will create a {@link CoreSession} only if it's required. The
     * returned object is a closable which will close the {@link CoreSession} if it was initialized.
     *
     * @param sessionProvider A factory to create the {@link CoreSession}.
     * @return A closable object.
     */
    public static SessionHandler openIfNeeded(SessionProvider sessionProvider) {
        SessionHandler handler = new SessionHandler(sessionProvider);
        return handler;
    }

    /**
     * It attaches the given session if the principal is the same than the given principal ({@link #attach(CoreSession)}
     * ). Otherwise, it opens a session using ({@link #open(NuxeoPrincipal)}).
     *
     * @param session The session to try to reuse.
     * @param principal The principal which must own the resulting session.
     * @return A closable object.
     */
    public static SessionHandler attachOrOpen(CoreSession session, NuxeoPrincipal principal) {
        if (session == null) {
            return open(principal);
        }
        if (principal.equals(session.getPrincipal())) {
            return attach(session);
        }
        return open(principal);
    }

    /**
     * It opens a system session.
     *
     * @see #open(NuxeoPrincipal)
     * @return A closable system session.
    public static SessionHandler openSystem() {
        return open(new SystemPrincipal(null));
    }

    /**
     * Remove the current session from the holder. It also closes the handled {@link CoreSession} if it's required.
     */
    @Override
    public void close() {
        if (sessionFactory != null) {
            // if it's managed externally, delegate the session closing
            sessionFactory.closeSession(session);
        } else if (mustClose && session != null) {
            // close the session if it wasn't attached or got back
            CoreInstance.closeCoreSession(session);
        }
        // clear the references
        this.session = null;
        this.sessionFactory = null;
        // if present, restore the existing session
        if (this.existing != null) {
            SessionHandler.holder.set(this.existing);
        }
    }

    /**
     * @see #close()
     */
    public static void closeCurrent() {
        SessionHandler handler = holder.get();
        if (handler == null) {
            throw new NoSessionException();
        }
        handler.close();
    }

    /**
     * Get the username of the current session's user.
     *
     * @return The username of the logged user.
     */
    public static String getUsername() {
        SessionHandler sessionHandler = holder.get();
        if (sessionHandler != null) {
            CoreSession session = sessionHandler.session;
            if (session != null && session.getPrincipal() != null) {
                return session.getPrincipal().getName();
            }
        }
        return null;
    }

    /**
     * Get the principal related to the current session.
     *
     * @return The principal logged.
     */
    public static NuxeoPrincipal getPrincipal() {
        SessionHandler sessionHandler = holder.get();
        if (sessionHandler != null) {
            CoreSession session = sessionHandler.session;
            if (session != null && session.getPrincipal() != null) {
                return (NuxeoPrincipal) session.getPrincipal();
            }
        }
        return null;
    }

    /**
     * Interface to provide {@link CoreSession} factory to {@link SessionHandler#openIfNeeded(SessionProvider)}.
     *
     */
    public interface SessionProvider {
        /**
         * Creates a CoreSession.
         *
         * @since BM
         */
        CoreSession getSession();

        /**
         * Close (or not) the session depending on the factory strategy.
         *
         * @param session The session handled.
         * @since BM
         */
        void closeSession(CoreSession session);
    }

}