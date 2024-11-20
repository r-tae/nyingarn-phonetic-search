package net.nyingarn;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.phonetic.PhoneticFilter;
import org.elasticsearch.plugin.Inject;
import org.elasticsearch.plugin.analysis.TokenFilterFactory;
import org.elasticsearch.plugin.NamedComponent;
import org.elasticsearch.plugin.settings.AnalysisSettings;
import org.elasticsearch.plugin.settings.BooleanSetting;

@NamedComponent(value = "nyingarn-phonetic")
public class PhoneticTokenFilterFactory implements TokenFilterFactory {

    private final Settings settings;

    @Inject
    public PhoneticTokenFilterFactory(Settings settings) { this.settings = settings; }

    @Override
    public TokenStream create(TokenStream tokenStream) {
        return  new PhoneticFilter(tokenStream, new NyingarnPhonetic(), !settings.replace());
    }

    @AnalysisSettings
    public interface Settings {
        @BooleanSetting(path = "replace", defaultValue = true)
        boolean replace();
    }
}


