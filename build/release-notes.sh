#!/usr/bin/bash

#
# Minimal bash script to generate the GitHub release notes
#
# Requires:
# * GitHub cli (gh)
# * jq
#


function query_milestones () {
    echo $(gh api graphql -F owner='chhorz' -F name='oas-generator' -f query='query($name: String!, $owner: String!) {
        repository(owner:$owner, name:$name){
            milestones(first: 10, states: OPEN, orderBy: { field: CREATED_AT, direction: ASC }) {
                nodes {
                    title,
                    number
                }
            }
        }
    }' | jq -r '[.data.repository.milestones.nodes[] | {number: .number, title: .title}]')
}

function query_issues () {
    echo $(gh api graphql -F owner='chhorz' -F name='oas-generator' -f query='query($name: String!, $owner: String!) {
        repository(owner:$owner, name:$name){
            milestone(number: '$number') {
               title,
               issues(first:100, labels:"'"$1"'", orderBy: { field: CREATED_AT, direction: DESC }) {
                   nodes {
                        number,
                        title
                        labels(first: 10) {
                           nodes{
                               name
                           }
                        }
                    }
                }
           }
       }
    }' | jq -r '[.data.repository.milestone.issues.nodes[] | {number: .number, title: .title}]')
}

function print_list () {
    for k in $(jq 'keys | .[]' <<< "$1")
    do
        title=$(jq -r ".[$k].title" <<< "$1")
        number=$(jq -r ".[$k].number" <<< "$1")
        printf '* %s (#%s)\n' "$title" "$number"
    done
}

function print_release_notes () {
    echo "-------------------------------------------------------------------------------------------------------------"
    echo "The first bugfix release version **$title** of the oas generator is available on maven central."
    echo ""
    echo "### Dependency"
    echo "\`\`\`xml"
    echo "<dependency>"
    echo "    <groupId>com.github.chhorz</groupId>"
    echo "    <artifactId>oas-generator-{spring-web|jaxrs|schema|asciidoctor}</artifactId>"
    echo "    <version>$title</version>"
    echo "</dependency>"
    echo "\`\`\`"
    echo ""
    echo "### Reference Documentation"
    echo "* [GitHub Page](https://chhorz.github.io/oas-generator/documentation/reference.html)"
    echo ""
    echo "### Issues"
    echo ""
    echo "#### Bugs"
    print_list "$bugs"
    echo ""
    echo "#### Features"
    print_list "$features"
    echo ""
    echo "#### Improvements"
    print_list "$improvements"
    echo "-------------------------------------------------------------------------------------------------------------"
}



milestones=$(query_milestones)

for m in $(jq 'keys | .[]' <<< "$milestones")
do
    title=$(jq -r ".[$m].title" <<< "$milestones")
    number=$(jq -r ".[$m].number" <<< "$milestones")

    echo "-------------------------------------------------------------------------------------------------------------"
    echo ""
    printf '    Milestone %s (#%s):\n' "$title" "$number"
    echo ""

    bugs=$(query_issues "type: bug")
    improvements=$(query_issues "type: improvement")
    features=$(query_issues "type: feature")

    print_release_notes
done

