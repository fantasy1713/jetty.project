//
//  ========================================================================
//  Copyright (c) 1995-2020 Mort Bay Consulting Pty Ltd and others.
//  ------------------------------------------------------------------------
//  All rights reserved. This program and the accompanying materials
//  are made available under the terms of the Eclipse Public License v1.0
//  and Apache License v2.0 which accompanies this distribution.
//
//      The Eclipse Public License is available at
//      http://www.eclipse.org/legal/epl-v10.html
//
//      The Apache License v2.0 is available at
//      http://www.opensource.org/licenses/apache2.0.php
//
//  You may elect to redistribute this code under either of these licenses.
//  ========================================================================
//

package org.eclipse.jetty.jaas.spi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jetty.security.UserPrincipal;
import org.eclipse.jetty.util.security.Credential;

/**
 * User
 *
 * Authentication information about a user. Also allows for
 * lazy loading of authorization (role) information for the user.
 *
 */
public class User
{

    protected UserPrincipal _userPrincipal;
    protected List<String> _roleNames = new ArrayList<>();
    protected boolean _rolesLoaded = false;

    /**
     * @param userPrincipal
     * @param roleNames
     */
    public User(UserPrincipal userPrincipal, List<String> roleNames)
    {
        _userPrincipal = userPrincipal;
        if (roleNames != null)
        {
            _roleNames.addAll(roleNames);
            _rolesLoaded = true;
        }
    }

    /**
     * @param userPrincipal
     */
    public User(UserPrincipal userPrincipal)
    {
        this(userPrincipal, null);
    }

    /**
     * Should be overridden by subclasses to obtain
     * role info
     *
     * @return List of role associated to the user
     * @throws Exception if the roles cannot be retrieved
     */
    public List<String> doFetchRoles()
        throws Exception
    {
        return Collections.emptyList();
    }

    public void fetchRoles() throws Exception
    {
        synchronized (_roleNames)
        {
            if (!_rolesLoaded)
            {
                _roleNames.addAll(doFetchRoles());
                _rolesLoaded = true;
            }
        }
    }

    public String getUserName()
    {
        return (_userPrincipal == null ? null : _userPrincipal.getName());
    }
    
    public UserPrincipal getUserPrincipal()
    {
        return _userPrincipal;
    }

    public List<String> getRoleNames()
    {
        return Collections.unmodifiableList(_roleNames);
    }

    public boolean checkCredential(Object suppliedCredential)
    {
        return _userPrincipal.authenticate(suppliedCredential);
    }
}
