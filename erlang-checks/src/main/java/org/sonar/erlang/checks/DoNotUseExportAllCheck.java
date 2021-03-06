/*
 * Sonar Erlang Plugin
 * Copyright (C) 2012 Tamas Kende
 * kende.tamas@gmail.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.erlang.checks;

import com.sonar.sslr.api.AstNode;

import org.sonar.squidbridge.checks.SquidCheck;
import org.sonar.check.BelongsToProfile;
import org.sonar.check.Cardinality;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.erlang.parser.ErlangGrammarImpl;
import org.sonar.sslr.parser.LexerlessGrammar;

@Rule(key = "DoNotUseExportAll", priority = Priority.MINOR, cardinality = Cardinality.SINGLE)
@BelongsToProfile(title = CheckList.REPOSITORY_NAME, priority = Priority.MAJOR)
public class DoNotUseExportAllCheck extends SquidCheck<LexerlessGrammar> {

  @RuleProperty(key = "skipInFlowControl", defaultValue = "true",
    description = "Set it false if you want to check export_all in flow controls.")
  private boolean skipInFlowControl = true;

  @Override
  public void init() {
    subscribeTo(ErlangGrammarImpl.compileAttr);
  }

  @Override
  public void visitNode(AstNode node) {
    if (hasFlowControlParent(node) && "export_all".equalsIgnoreCase(node.getFirstChild(ErlangGrammarImpl.primaryExpression).getTokenOriginalValue())) {
      getContext().createLineViolation(this, "Do not use export_all", node);
    }
  }

  private boolean hasFlowControlParent(AstNode astNode) {
    return !astNode.hasAncestor(ErlangGrammarImpl.flowControlAttr) || !skipInFlowControl;
  }

  public void setsSkipInFlowControl(boolean skipInFlowControl) {
    this.skipInFlowControl = skipInFlowControl;
  }

}
