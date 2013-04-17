package br.usp.ime.cogroo.exceptions;

import org.cogroo.exceptions.InternationalizedException;


public class CommunityException extends InternationalizedException {

	private static final long serialVersionUID = 3580195184638793206L;
	public static final String STANDARD_MESSAGE_CATALOG = "CommunityException_Messages";

	/**
	 * Creates a new exception with a null message.
	 */
	public CommunityException() {
		super();
	}

	/**
	 * Creates a new exception with the specified cause and a null message.
	 * 
	 * @param aCause
	 *            the original exception that caused this exception to be
	 *            thrown, if any
	 */
	public CommunityException(Throwable aCause) {
		super(aCause);
	}

	/**
	 * Creates a new exception with a the specified message.
	 * 
	 * @param aResourceBundleName
	 *            the base name of the resource bundle in which the message for
	 *            this exception is located.
	 * @param aMessageKey
	 *            an identifier that maps to the message for this exception. The
	 *            message may contain placeholders for arguments as defined by
	 *            the {@link java.text.MessageFormat MessageFormat} class.
	 * @param aArguments
	 *            The arguments to the message. <code>null</code> may be used if
	 *            the message has no arguments.
	 */
	public CommunityException(String aResourceBundleName, String aMessageKey,
			Object[] aArguments) {
		super(aResourceBundleName, aMessageKey, aArguments);
	}

	/**
	 * Creates a new exception with the specified message and cause.
	 * 
	 * @param aResourceBundleName
	 *            the base name of the resource bundle in which the message for
	 *            this exception is located.
	 * @param aMessageKey
	 *            an identifier that maps to the message for this exception. The
	 *            message may contain placeholders for arguments as defined by
	 *            the {@link java.text.MessageFormat MessageFormat} class.
	 * @param aArguments
	 *            The arguments to the message. <code>null</code> may be used if
	 *            the message has no arguments.
	 * @param aCause
	 *            the original exception that caused this exception to be
	 *            thrown, if any
	 */
	public CommunityException(String aResourceBundleName, String aMessageKey,
			Object[] aArguments, Throwable aCause) {
		super(aResourceBundleName, aMessageKey, aArguments, aCause);
	}

	/**
	 * Creates a new exception with a message from the
	 * {@link #STANDARD_MESSAGE_CATALOG}.
	 * 
	 * @param aMessageKey
	 *            an identifier that maps to the message for this exception. The
	 *            message may contain placeholders for arguments as defined by
	 *            the {@link java.text.MessageFormat MessageFormat} class.
	 * @param aArguments
	 *            The arguments to the message. <code>null</code> may be used if
	 *            the message has no arguments.
	 */
	public CommunityException(String aMessageKey, Object[] aArguments) {
		super(STANDARD_MESSAGE_CATALOG, aMessageKey, aArguments);
	}

	/**
	 * Creates a new exception with the specified cause and a message from the
	 * {@link #STANDARD_MESSAGE_CATALOG}.
	 * 
	 * @param aMessageKey
	 *            an identifier that maps to the message for this exception. The
	 *            message may contain placeholders for arguments as defined by
	 *            the {@link java.text.MessageFormat MessageFormat} class.
	 * @param aArguments
	 *            The arguments to the message. <code>null</code> may be used if
	 *            the message has no arguments.
	 * @param aCause
	 *            the original exception that caused this exception to be
	 *            thrown, if any
	 */
	public CommunityException(String aMessageKey, Object[] aArguments,
			Throwable aCause) {
		super(STANDARD_MESSAGE_CATALOG, aMessageKey, aArguments, aCause);
	}
}
