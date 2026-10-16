package io.github.bownlux.language.ruby;

import net.fabricmc.loader.language.LanguageAdapter;
import net.fabricmc.loader.language.LanguageAdapterException;

/**
 * RubyLanguageAdapter
 */

@SuppressWarnings("deprecation")
public class RubyLanguageAdapter implements LanguageAdapter {

    @Override
    public Object createInstance(Class<?> baseClass, Options options) throws LanguageAdapterException {
        throw new UnsupportedOperationException("Unimplemented method 'createInstance'");
    }
}
