package dev.fiki.forgehax.api.cmd.settings;

import dev.fiki.forgehax.api.cmd.AbstractSetting;
import dev.fiki.forgehax.api.cmd.IParentCommand;
import dev.fiki.forgehax.api.cmd.flag.EnumFlag;
import dev.fiki.forgehax.api.typeconverter.IConverter;
import dev.fiki.forgehax.api.typeconverter.TypeConverters;
import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;

import java.util.List;
import java.util.Set;

public final class CaseInsensitiveString extends AbstractSetting<String> {
  @Builder
  public CaseInsensitiveString(IParentCommand parent,
      String name, @Singular Set<String> aliases, String description,
      @Singular Set<EnumFlag> flags, @Singular List<ISettingValueChanged<String>> changedListeners,
      @NonNull String defaultTo) {
    super(parent, name, aliases, description, flags, defaultTo, null, null);
    addListeners(ISettingValueChanged.class, changedListeners);
    onFullyConstructed();
  }

  @Override
  public IConverter<String> getConverter() {
    return TypeConverters.STRING_CASE_INSENSITIVE;
  }

  @Override
  protected int getMaxArguments() {
    return Integer.MAX_VALUE;
  }
}
