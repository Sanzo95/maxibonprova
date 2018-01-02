package com.jsoniter.extra;

import com.jsoniter.spi.*;

/**
 * Public Class NamingStrategy.
 * 
 * @author MaxiBon
 *
 */
public class NamingStrategySupport {

	private NamingStrategySupport() {
	}

	/**
	 * Public Interface NamingStrategy.
	 * 
	 * @author MaxiBon
	 *
	 */
	public interface NamingStrategy {
		/**
		 *String translate(String input);
		 * 
		 * @author MaxiBon
		 *
		 */
		
		String translate(String input);
	}

	/**
	 * enable
	 * 
	 */

	public static void enable(final NamingStrategy namingStrategy) {
		boolean enabled = false;
		synchronized (NamingStrategySupport.class) {
			if (enabled) {
				throw new JsonException("NamingStrategySupport.enable can only be called once");
			}
			enabled = true;
			JsoniterSpi.registerExtension(new EmptyExtension() {
				/**
				 * public void updateClassDescriptor(ClassDescriptor desc)
				 * 
				 * @author MaxiBon
				 *
				 */
				@Override
				public void updateClassDescriptor(ClassDescriptor desc) {
					for (Binding binding : desc.allBindings()) {
						String translated = namingStrategy.translate(binding.name);
						binding.toNames = new String[] { translated };
						binding.fromNames = new String[] { translated };
					}
				}
			});
		}
	}

	/**
	 * public static NamingStrategy SNAKE_CASE = new NamingStrategy()
	 * 
	 * @author MaxiBon
	 *
	 */

	public static NamingStrategy SNAKE_CASE = new NamingStrategy() {
		@Override
		public String translate(String stringa1) {
			if (stringa1 == null) {
				return stringa1; // garbage in, garbage out
			}

			int length = stringa1.length();
			StringBuilder result = new StringBuilder(length * 2);
			int resultLength = 0;
			boolean wasPrevTranslated = false;
			for (int i = 0; i < length; i++) {
				char c = stringa1.charAt(i);
				if (i > 0 || c != '_') // skip first starting underscore
				{
					if (Character.isUpperCase(c)) {
						if (!wasPrevTranslated && resultLength > 0 && result.charAt(resultLength - 1) != '_') {
							result.append('_');
							resultLength++;
						}
						c = Character.toLowerCase(c);
						wasPrevTranslated = true;
					} else {
						wasPrevTranslated = false;
					}
					result.append(c);
					resultLength++;
				}
			}
			return resultLength > 0 ? result.toString() : stringa1;
		}
	};
	/**
	 * public static NamingStrategy UPPER_CAMEL_CASE = new NamingStrategy()
	 * 
	 * @author MaxiBon
	 *
	 */

	public NamingStrategy UPPER_CAMEL_CASE = new NamingStrategy() {
		@Override
		public String translate(String stringa2) {
			if (stringa2 == null || stringa2.length() == 0) {
				return stringa2; // garbage in, garbage out
			}
			// Replace first lower-case letter with upper-case equivalent
			char c = stringa2.charAt(0);
			char uc = Character.toUpperCase(c);
			if (c == uc) {
				return stringa2;
			}
			StringBuilder sb = new StringBuilder(stringa2);
			sb.setCharAt(0, uc);
			return sb.toString();
		}
	};
	/**
	 * public static NamingStrategy LOWER_CASE = new NamingStrategy()
	 * 
	 * @author MaxiBon
	 *
	 */

	public NamingStrategy LOWER_CASE = new NamingStrategy() {
		@Override
		public String translate(String stringa3) {
			return stringa3.toLowerCase();
		}
	};
	/**
	 * public NamingStrategy KEBAB_CASE = new NamingStrategy()
	 * 
	 * @author MaxiBon
	 *
	 */

	public NamingStrategy KEBAB_CASE = new NamingStrategy() {
		@Override
		public String translate(String stringa4) {
			if (stringa4 == null) {
				return stringa4; // garbage in, garbage out
			}

			int length = stringa4.length();
			if (length == 0) {
				return stringa4;
			}

			StringBuilder result = new StringBuilder(length + (length >> 1));

			int upperCount = 0;

			for (int i = 0; i < length; ++i) {
				char ch = stringa4.charAt(i);
				char lc = Character.toLowerCase(ch);

				if (lc == ch) { // lower-case letter means we can get new word
					// but need to check for multi-letter upper-case (acronym),
					// where assumption
					// is that the last upper-case char is start of a new word
					if (upperCount > 1) {
						// so insert hyphen before the last character now
						result.insert(result.length() - 1, '-');
					}
					upperCount = 0;
				} else {
					// Otherwise starts new word, unless beginning of string
					if ((upperCount == 0) && (i > 0)) {
						result.append('-');
					}
					++upperCount;
				}
				result.append(lc);
			}
			return result.toString();
		}
	};
}
