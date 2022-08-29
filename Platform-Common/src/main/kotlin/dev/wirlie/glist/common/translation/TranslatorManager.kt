package dev.wirlie.glist.common.translation

import dev.wirlie.glist.common.Platform
import dev.wirlie.glist.common.configuration.sections.GeneralSection

class TranslatorManager(
    val platform: Platform<*, *, *>
) {

    private var code: String = platform.configuration.getSection(GeneralSection::class.java)?.language?: "en"
    private var translator: Translator? = null

    fun getTranslator(): Translator {
        if(translator != null) {
            return translator!!
        }

        translator = Translator(platform, code)
        return translator!!
    }

}
